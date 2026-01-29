package com.biostar.camtest

import android.Manifest
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.usb.UsbDevice
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.ICameraHelper
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var textureView: TextureView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnFullscreen: Button
    private lateinit var btnCapture: Button
    private lateinit var btnExitFullscreen: Button
    private lateinit var btnCaptureFullscreen: Button
    private lateinit var btnRefresh: Button
    private lateinit var cameraSpinner: Spinner
    private lateinit var placeholder: LinearLayout
    private lateinit var header: LinearLayout
    private lateinit var controls: LinearLayout
    private lateinit var statusText: TextView
    
    private var mCameraHelper: ICameraHelper? = null
    private var isFullscreen = false
    private var isCameraRunning = false
    private var usbDeviceList: MutableList<UsbDevice> = mutableListOf()
    private var selectedDeviceIndex = 0
    private var mSurface: Surface? = null
    private var previewWidth = 640
    private var previewHeight = 480
    
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
    
    private val mStateCallback = object : ICameraHelper.StateCallback {
        override fun onAttach(device: UsbDevice) {
            runOnUiThread {
                if (!usbDeviceList.any { it.deviceId == device.deviceId }) {
                    usbDeviceList.add(device)
                    updateDeviceSpinner()
                }
                statusText.text = "📷 Cámara USB detectada: ${device.productName ?: device.deviceName}"
                Toast.makeText(this@MainActivity, "Cámara USB conectada!", Toast.LENGTH_SHORT).show()
            }
        }
        
        override fun onDeviceOpen(device: UsbDevice, isFirstOpen: Boolean) {
            runOnUiThread {
                statusText.text = "🔄 Dispositivo abierto, abriendo cámara..."
                try {
                    mCameraHelper?.openCamera()
                } catch (e: Exception) {
                    statusText.text = "❌ Error openCamera: ${e.message}"
                }
            }
        }
        
        override fun onCameraOpen(device: UsbDevice) {
            runOnUiThread {
                try {
                    mCameraHelper?.let { helper ->
                        // Configurar resolución si es posible
                        try {
                            val sizes = helper.supportedSizeList
                            if (sizes != null && sizes.isNotEmpty()) {
                                val targetSize = sizes.firstOrNull { it.width == 1280 && it.height == 720 }
                                    ?: sizes.firstOrNull { it.width == 1920 && it.height == 1080 }
                                    ?: sizes.firstOrNull { it.width == 640 && it.height == 480 }
                                    ?: sizes[0]
                                helper.previewSize = targetSize
                                previewWidth = targetSize.width
                                previewHeight = targetSize.height
                                statusText.text = "📷 ${previewWidth}x${previewHeight}"
                            }
                        } catch (e: Exception) {
                            statusText.text = "📷 Usando resolución default"
                        }
                        
                        // Agregar surface
                        if (textureView.isAvailable) {
                            val surfaceTexture = textureView.surfaceTexture
                            if (surfaceTexture != null) {
                                mSurface = Surface(surfaceTexture)
                                helper.addSurface(mSurface, false)
                            }
                        }
                        
                        // Iniciar preview
                        helper.startPreview()
                        
                        // Ajustar aspect ratio
                        adjustAspectRatio()
                        
                        isCameraRunning = true
                        placeholder.visibility = View.GONE
                        updateUI()
                        Toast.makeText(this@MainActivity, "Cámara lista!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    statusText.text = "❌ Error: ${e.message}"
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
        
        override fun onCameraClose(device: UsbDevice) {
            runOnUiThread {
                isCameraRunning = false
                updateUI()
            }
        }
        
        override fun onDeviceClose(device: UsbDevice) {
            runOnUiThread {
                isCameraRunning = false
                updateUI()
            }
        }
        
        override fun onDetach(device: UsbDevice) {
            runOnUiThread {
                usbDeviceList.removeAll { it.deviceId == device.deviceId }
                updateDeviceSpinner()
                statusText.text = "📷 Cámara USB desconectada"
                Toast.makeText(this@MainActivity, "Cámara USB desconectada", Toast.LENGTH_SHORT).show()
            }
        }
        
        override fun onCancel(device: UsbDevice) {
            runOnUiThread {
                statusText.text = "⚠️ Permiso USB denegado"
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Iniciar en vertical
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // Mantener pantalla encendida
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Inicializar vistas
        textureView = findViewById(R.id.textureView)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnFullscreen = findViewById(R.id.btnFullscreen)
        btnCapture = findViewById(R.id.btnCapture)
        btnExitFullscreen = findViewById(R.id.btnExitFullscreen)
        btnCaptureFullscreen = findViewById(R.id.btnCaptureFullscreen)
        btnRefresh = findViewById(R.id.btnRefresh)
        cameraSpinner = findViewById(R.id.cameraSpinner)
        placeholder = findViewById(R.id.placeholder)
        header = findViewById(R.id.header)
        controls = findViewById(R.id.controls)
        statusText = findViewById(R.id.statusText)
        
        // Configurar TextureView
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                // Surface disponible, se usará cuando la cámara esté abierta
            }
            
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                if (isCameraRunning) {
                    adjustAspectRatio()
                }
            }
            
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                try {
                    mSurface?.let { 
                        mCameraHelper?.removeSurface(it) 
                    }
                } catch (e: Exception) {
                    // Ignorar
                }
                mSurface = null
                return true
            }
            
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
        
        // Configurar botones
        btnStart.setOnClickListener {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
        
        btnStop.setOnClickListener {
            stopCamera()
        }
        
        btnFullscreen.setOnClickListener {
            toggleFullscreen()
        }
        
        btnExitFullscreen.setOnClickListener {
            toggleFullscreen()
        }
        
        btnCapture.setOnClickListener {
            takePhoto()
        }
        
        btnCaptureFullscreen.setOnClickListener {
            takePhoto()
        }
        
        btnRefresh.setOnClickListener {
            refreshDevices()
        }
        
        // Ya no usamos tap para salir - ahora hay botón de captura
        // textureView.setOnClickListener { }
        
        // Configurar spinner de cámaras
        cameraSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (selectedDeviceIndex != position && usbDeviceList.isNotEmpty()) {
                    selectedDeviceIndex = position
                    if (isCameraRunning) {
                        // Cambiar a otra cámara
                        stopCamera()
                        startCamera()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Verificar permisos
        if (allPermissionsGranted()) {
            initCameraHelper()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }
    
    private fun initCameraHelper() {
        try {
            mCameraHelper = CameraHelper().apply {
                setStateCallback(mStateCallback)
            }
            statusText.text = "🔍 Buscando cámaras USB..."
        } catch (e: Exception) {
            statusText.text = "❌ Error init: ${e.message}"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun refreshDevices() {
        try {
            usbDeviceList.clear()
            mCameraHelper?.release()
            mCameraHelper = null
            mSurface = null
            initCameraHelper()
            Toast.makeText(this, "Buscando dispositivos USB...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun updateDeviceSpinner() {
        val deviceNames = if (usbDeviceList.isEmpty()) {
            listOf("No hay cámaras USB")
        } else {
            usbDeviceList.mapIndexed { index, device ->
                "Cámara ${index + 1}: ${device.productName ?: device.deviceName}"
            }
        }
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cameraSpinner.adapter = adapter
        cameraSpinner.visibility = View.VISIBLE
        
        btnStart.isEnabled = usbDeviceList.isNotEmpty()
    }
    
    private fun startCamera() {
        if (usbDeviceList.isEmpty()) {
            Toast.makeText(this, "No hay cámaras USB conectadas", Toast.LENGTH_SHORT).show()
            return
        }
        
        val device = usbDeviceList.getOrNull(selectedDeviceIndex) ?: usbDeviceList[0]
        mCameraHelper?.selectDevice(device)
        
        placeholder.visibility = View.GONE
        statusText.text = "🔄 Conectando a ${device.productName ?: device.deviceName}..."
    }
    
    private fun stopCamera() {
        try {
            mCameraHelper?.stopPreview()
            mCameraHelper?.closeCamera()
        } catch (e: Exception) {
            // Ignorar errores al detener
        }
        isCameraRunning = false
        placeholder.visibility = View.VISIBLE
        updateUI()
        statusText.text = "⏹️ Cámara detenida"
    }
    
    private fun updateUI() {
        btnStart.visibility = if (isCameraRunning) View.GONE else View.VISIBLE
        btnStop.visibility = if (isCameraRunning) View.VISIBLE else View.GONE
        btnFullscreen.visibility = if (isCameraRunning) View.VISIBLE else View.GONE
        btnCapture.visibility = if (isCameraRunning) View.VISIBLE else View.GONE
    }
    
    private fun adjustAspectRatio() {
        textureView.post {
            val viewWidth = textureView.width.toFloat()
            val viewHeight = textureView.height.toFloat()
            
            if (viewWidth == 0f || viewHeight == 0f || previewWidth == 0 || previewHeight == 0) {
                return@post
            }
            
            val aspectRatio = previewWidth.toFloat() / previewHeight.toFloat()
            val viewAspectRatio = viewWidth / viewHeight
            
            val matrix = Matrix()
            val scaleX: Float
            val scaleY: Float
            
            if (viewAspectRatio > aspectRatio) {
                // View es más ancho que el preview - ajustar ancho
                scaleX = aspectRatio / viewAspectRatio
                scaleY = 1f
            } else {
                // View es más alto que el preview - ajustar alto
                scaleX = 1f
                scaleY = viewAspectRatio / aspectRatio
            }
            
            // Centrar la transformación
            val pivotX = viewWidth / 2f
            val pivotY = viewHeight / 2f
            
            matrix.setScale(scaleX, scaleY, pivotX, pivotY)
            textureView.setTransform(matrix)
        }
    }
    
    private fun takePhoto() {
        if (!isCameraRunning) {
            Toast.makeText(this, "La cámara no está activa", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Capturar frame del TextureView
        val bitmap = textureView.bitmap
        if (bitmap != null) {
            saveBitmapToGallery(bitmap)
        } else {
            Toast.makeText(this, "Error al capturar imagen", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "USBCAM_$name")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/USB-CAM")
            }
        }
        
        try {
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                val outputStream: OutputStream? = contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                }
                Toast.makeText(this, "📸 Foto guardada en Galería/USB-CAM", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun toggleFullscreen() {
        isFullscreen = !isFullscreen
        
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        
        if (isFullscreen) {
            // Cambiar a horizontal
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            header.visibility = View.GONE
            controls.visibility = View.GONE
            btnExitFullscreen.visibility = View.VISIBLE
            btnCaptureFullscreen.visibility = View.VISIBLE
            supportActionBar?.hide()
        } else {
            // Volver a vertical
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            header.visibility = View.VISIBLE
            controls.visibility = View.VISIBLE
            btnExitFullscreen.visibility = View.GONE
            btnCaptureFullscreen.visibility = View.GONE
            supportActionBar?.show()
        }
        
        // Reajustar aspect ratio después del cambio de layout
        textureView.postDelayed({ adjustAspectRatio() }, 300)
    }
    
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initCameraHelper()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reajustar aspect ratio después del cambio de orientación
        if (isCameraRunning) {
            textureView.postDelayed({ adjustAspectRatio() }, 200)
        }
    }
    
    override fun onStart() {
        super.onStart()
        // No hacer nada aquí - el surface se agrega en onCameraOpen
    }
    
    override fun onStop() {
        super.onStop()
        try {
            mCameraHelper?.stopPreview()
        } catch (e: Exception) {
            // Ignorar
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            mCameraHelper?.release()
            mCameraHelper = null
            mSurface = null
        } catch (e: Exception) {
            // Ignorar
        }
    }
}
