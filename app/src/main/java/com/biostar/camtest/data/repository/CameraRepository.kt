package com.biostar.camtest.data.repository

import android.content.Context
import android.hardware.usb.UsbDevice
import com.biostar.camtest.data.models.CameraResolution
import com.biostar.camtest.data.models.UsbCameraDevice
import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.ICameraHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cameraHelper: ICameraHelper? = null
    
    private val _connectedDevices = MutableStateFlow<List<UsbCameraDevice>>(emptyList())
    val connectedDevices: StateFlow<List<UsbCameraDevice>> = _connectedDevices.asStateFlow()
    
    private val _currentDevice = MutableStateFlow<UsbCameraDevice?>(null)
    val currentDevice: StateFlow<UsbCameraDevice?> = _currentDevice.asStateFlow()
    
    fun observeCameraEvents(): Flow<CameraEvent> = callbackFlow {
        val helper = CameraHelper()
        cameraHelper = helper
        
        val callback = object : ICameraHelper.StateCallback {
            override fun onAttach(device: UsbDevice) {
                Timber.d("Camera attached: ${device.productName}")
                trySend(CameraEvent.DeviceAttached(device))
            }
            
            override fun onDeviceOpen(device: UsbDevice, isFirstOpen: Boolean) {
                Timber.d("Device opened: ${device.productName}")
                trySend(CameraEvent.DeviceOpened(device))
            }
            
            override fun onCameraOpen(device: UsbDevice) {
                Timber.d("Camera opened: ${device.productName}")
                
                val resolutions = helper.supportedSizeList?.map { size ->
                    CameraResolution(size.width, size.height, 30)
                } ?: emptyList()
                
                val cameraDevice = UsbCameraDevice(
                    device = device,
                    name = device.productName ?: "USB Camera",
                    availableResolutions = resolutions
                )
                
                _currentDevice.value = cameraDevice
                trySend(CameraEvent.CameraReady(cameraDevice))
            }
            
            override fun onCameraClose(device: UsbDevice) {
                Timber.d("Camera closed: ${device.productName}")
                _currentDevice.value = null
                trySend(CameraEvent.CameraClosed(device))
            }
            
            override fun onDeviceClose(device: UsbDevice) {
                Timber.d("Device closed: ${device.productName}")
                _currentDevice.value = null
                trySend(CameraEvent.DeviceClosed(device))
            }
            
            override fun onDetach(device: UsbDevice) {
                Timber.d("Camera detached: ${device.productName}")
                _currentDevice.value = null
                trySend(CameraEvent.DeviceDetached(device))
            }
            
            override fun onCancel(device: UsbDevice) {
                Timber.d("Permission cancelled: ${device.productName}")
                trySend(CameraEvent.PermissionDenied(device))
            }
        }
        
        helper.setStateCallback(callback)
        
        awaitClose {
            Timber.d("Closing camera repository")
            helper.release()
            cameraHelper = null
        }
    }
    
    fun getCameraHelper(): ICameraHelper? = cameraHelper
    
    fun selectDevice(device: UsbDevice) {
        Timber.d("Selecting device: ${device.productName}")
        cameraHelper?.selectDevice(device)
    }
    
    fun openCamera() {
        Timber.d("Opening camera")
        cameraHelper?.openCamera()
    }
    
    fun closeCamera() {
        Timber.d("Closing camera")
        cameraHelper?.closeCamera()
    }
    
    fun startPreview() {
        Timber.d("Starting preview")
        cameraHelper?.startPreview()
    }
    
    fun stopPreview() {
        Timber.d("Stopping preview")
        cameraHelper?.stopPreview()
    }
    
    fun setResolution(resolution: CameraResolution) {
        Timber.d("Setting resolution: $resolution")
        cameraHelper?.previewSize = com.serenegiant.usb.Size(resolution.width, resolution.height)
    }
}

sealed class CameraEvent {
    data class DeviceAttached(val device: UsbDevice) : CameraEvent()
    data class DeviceOpened(val device: UsbDevice) : CameraEvent()
    data class CameraReady(val cameraDevice: UsbCameraDevice) : CameraEvent()
    data class CameraClosed(val device: UsbDevice) : CameraEvent()
    data class DeviceClosed(val device: UsbDevice) : CameraEvent()
    data class DeviceDetached(val device: UsbDevice) : CameraEvent()
    data class PermissionDenied(val device: UsbDevice) : CameraEvent()
}
