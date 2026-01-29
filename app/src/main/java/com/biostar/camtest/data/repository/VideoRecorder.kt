package com.biostar.camtest.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.herohan.uvcapp.ICameraHelper
import com.herohan.uvcapp.VideoCapture
import com.serenegiant.utils.FileUtils
import com.serenegiant.utils.UriHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File

class VideoRecorder(
    private val context: Context,
    private val cameraHelper: ICameraHelper,
    private var audioConfig: AudioConfig = AudioConfig()
) {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()
    
    private val _recordingDuration = MutableStateFlow(0L)
    val recordingDuration: StateFlow<Long> = _recordingDuration.asStateFlow()
    
    private var startTime: Long = 0
    
    fun updateAudioConfig(config: AudioConfig) {
        audioConfig = config
        Timber.d("Audio config updated: bitrate=${config.bitrate}, sampleRate=${config.sampleRate}")
    }
    
    fun startRecording(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Create file in Movies directory with timestamp
            val timestamp = System.currentTimeMillis()
            val fileName = "USB_CAM_$timestamp.mp4"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "USBCam/$fileName")
            file.parentFile?.mkdirs()
            
            val options = VideoCapture.OutputFileOptions.Builder(file).build()
            
            // Alternative: Save to MediaStore directly
            // val contentValues = ContentValues().apply {
            //     put(MediaStore.MediaColumns.DISPLAY_NAME, "USB_CAM_${System.currentTimeMillis()}")
            //     put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            //     put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/USBCam")
            // }
            // val options = VideoCapture.OutputFileOptions.Builder(
            //     context.contentResolver,
            //     MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            //     contentValues
            // ).build()
            
            cameraHelper.startRecording(options, object : VideoCapture.OnVideoCaptureCallback {
                override fun onStart() {
                    Timber.d("Video recording started")
                    _isRecording.value = true
                    startTime = System.currentTimeMillis()
                }
                
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    val path = UriHelper.getPath(context, outputFileResults.savedUri)
                    Timber.d("Video saved: $path")
                    _isRecording.value = false
                    _recordingDuration.value = 0
                    onSuccess(path ?: "Video guardado")
                }
                
                override fun onError(
                    videoCaptureError: Int,
                    message: String,
                    cause: Throwable?
                ) {
                    Timber.e(cause, "Video recording error: $message")
                    _isRecording.value = false
                    _recordingDuration.value = 0
                    onError(message)
                }
            })
        } catch (e: Exception) {
            Timber.e(e, "Error starting video recording")
            onError(e.message ?: "Error desconocido")
        }
    }
    
    fun stopRecording() {
        try {
            cameraHelper.stopRecording()
            Timber.d("Video recording stopped")
        } catch (e: Exception) {
            Timber.e(e, "Error stopping video recording")
        }
    }
    
    fun updateDuration() {
        if (_isRecording.value) {
            _recordingDuration.value = System.currentTimeMillis() - startTime
        }
    }
}
