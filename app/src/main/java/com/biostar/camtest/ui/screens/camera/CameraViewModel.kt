package com.biostar.camtest.ui.screens.camera

import android.graphics.Bitmap
import android.view.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biostar.camtest.data.models.*
import com.biostar.camtest.data.preferences.UserPreferences
import com.biostar.camtest.data.repository.CameraEvent
import com.biostar.camtest.data.repository.CameraRepository
import com.biostar.camtest.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class CameraUiState(
    val cameraState: CameraState = CameraState.Idle,
    val availableDevices: List<UsbCameraDevice> = emptyList(),
    val selectedDevice: UsbCameraDevice? = null,
    val selectedResolution: CameraResolution? = null,
    val availableResolutions: List<CameraResolution> = emptyList(),
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0,
    val gridOverlay: GridOverlay = GridOverlay(),
    val isMirrored: Boolean = false,
    val rotation: Int = 0,
    val isFullscreen: Boolean = false,
    val isMuted: Boolean = false,
    val showResolutionSheet: Boolean = false,
    val showDeviceSheet: Boolean = false,
    val showCameraControls: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val mediaRepository: MediaRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var currentSurface: Surface? = null

    init {
        observeCameraEvents()
        loadUserPreferences()
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            // Load grid preferences
            launch {
                combine(
                    userPreferences.defaultGridType,
                    userPreferences.gridEnabledByDefault
                ) { values: Array<*> ->
                    Pair(values[0] as GridType, values[1] as Boolean)
                }.collect { (gridType, enabled) ->
                    _uiState.update { state ->
                        state.copy(
                            gridOverlay = GridOverlay(
                                enabled = enabled,
                                type = gridType
                            )
                        )
                    }
                }
            }
            
            // Load audio preferences
            launch {
                combine(
                    userPreferences.audioBitrate,
                    userPreferences.audioSampleRate
                ) { values: Array<*> ->
                    Pair(values[0] as Int, values[1] as Int)
                }.collect { (bitrate, sampleRate) ->
                    cameraRepository.updateAudioConfig(bitrate, sampleRate)
                }
            }
        }
    }

    private fun observeCameraEvents() {
        viewModelScope.launch {
            // Observe recording state
            launch {
                cameraRepository.isRecording.collect { recording ->
                    _uiState.update { it.copy(isRecording = recording) }
                }
            }
            
            // Observe recording duration
            launch {
                cameraRepository.recordingDuration.collect { duration ->
                    _uiState.update { it.copy(recordingDuration = duration) }
                }
            }
            
            cameraRepository.observeCameraEvents().collect { event ->
                when (event) {
                    is CameraEvent.DeviceAttached -> {
                        Timber.d("Device attached in ViewModel")
                    }
                    is CameraEvent.CameraReady -> {
                        handleCameraReady(event.cameraDevice)
                    }
                    is CameraEvent.DeviceDetached -> {
                        handleDeviceDetached()
                    }
                    is CameraEvent.PermissionDenied -> {
                        _uiState.update { it.copy(
                            cameraState = CameraState.Error("Permiso USB denegado"),
                            errorMessage = "Permiso USB denegado"
                        )}
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleCameraReady(device: UsbCameraDevice) {
        Timber.d("Camera ready: ${device.displayName}")
        
        val defaultResolution = device.availableResolutions.firstOrNull { 
            it.width == 1920 && it.height == 1080 
        } ?: device.availableResolutions.firstOrNull()
        
        _uiState.update { 
            it.copy(
                cameraState = CameraState.Connected(device, defaultResolution ?: CameraResolution(640, 480, 30)),
                selectedDevice = device,
                availableResolutions = device.availableResolutions,
                selectedResolution = defaultResolution,
                errorMessage = null
            )
        }
        
        defaultResolution?.let { setResolution(it) }
    }

    private fun handleDeviceDetached() {
        Timber.d("Device detached in ViewModel")
        _uiState.update { 
            it.copy(
                cameraState = CameraState.Idle,
                selectedDevice = null,
                selectedResolution = null,
                availableResolutions = emptyList(),
                isRecording = false
            )
        }
    }

    fun startCamera() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(cameraState = CameraState.Connecting) }
                cameraRepository.openCamera()
            } catch (e: Exception) {
                Timber.e(e, "Error starting camera")
                _uiState.update { 
                    it.copy(
                        cameraState = CameraState.Error(e.message ?: "Error desconocido"),
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun stopCamera() {
        viewModelScope.launch {
            try {
                if (_uiState.value.isRecording) {
                    cameraRepository.stopRecording()
                }
                cameraRepository.stopPreview()
                cameraRepository.closeCamera()
                _uiState.update { it.copy(cameraState = CameraState.Idle) }
            } catch (e: Exception) {
                Timber.e(e, "Error stopping camera")
            }
        }
    }

    fun setSurface(surface: Surface) {
        currentSurface = surface
        cameraRepository.getCameraHelper()?.addSurface(surface, false)
    }

    fun removeSurface() {
        currentSurface?.let { surface ->
            cameraRepository.getCameraHelper()?.removeSurface(surface)
        }
        currentSurface = null
    }

    fun setResolution(resolution: CameraResolution) {
        viewModelScope.launch {
            try {
                cameraRepository.setResolution(resolution)
                _uiState.update { it.copy(selectedResolution = resolution) }
                Timber.d("Resolution set to: $resolution")
            } catch (e: Exception) {
                Timber.e(e, "Error setting resolution")
            }
        }
    }

    fun toggleRecording() {
        viewModelScope.launch {
            if (cameraRepository.isRecordingActive()) {
                // Stop recording
                cameraRepository.stopRecording()
                Timber.d("Recording stopped")
            } else {
                // Start recording
                cameraRepository.startRecording(
                    onSuccess = { path ->
                        Timber.d("Video saved: $path")
                        // TODO: Show success message to user
                    },
                    onError = { error ->
                        Timber.e("Recording error: $error")
                        _uiState.update { 
                            it.copy(
                                errorMessage = "Error al grabar: $error"
                            )
                        }
                    }
                )
                Timber.d("Recording started")
            }
        }
    }

    fun takePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val resolution = _uiState.value.selectedResolution?.toString() ?: "Unknown"
                val result = mediaRepository.savePhoto(bitmap, resolution)
                
                result.onSuccess {
                    Timber.d("Photo saved successfully")
                }.onFailure { error ->
                    Timber.e(error, "Error saving photo")
                    _uiState.update { it.copy(errorMessage = "Error al guardar foto") }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error taking photo")
            }
        }
    }

    fun toggleGridOverlay() {
        _uiState.update { 
            it.copy(gridOverlay = it.gridOverlay.copy(enabled = !it.gridOverlay.enabled))
        }
    }

    fun setGridType(type: GridType) {
        _uiState.update { 
            it.copy(gridOverlay = it.gridOverlay.copy(type = type))
        }
    }

    fun toggleMirror() {
        _uiState.update { it.copy(isMirrored = !it.isMirrored) }
    }

    fun rotateCamera() {
        _uiState.update { 
            it.copy(rotation = (it.rotation + 90) % 360)
        }
    }

    fun toggleFullscreen() {
        _uiState.update { it.copy(isFullscreen = !it.isFullscreen) }
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun showResolutionSheet(show: Boolean) {
        _uiState.update { it.copy(showResolutionSheet = show) }
    }
    
    fun showCameraControls(show: Boolean) {
        if (show) {
            cameraRepository.initializeCameraControls()
        }
        _uiState.update { it.copy(showCameraControls = show) }
    }
    
    fun updateCameraControl(controlId: Int, value: Int) {
        cameraRepository.getCameraControlsManager()?.setControlValue(controlId, value)
    }

    fun showDeviceSheet(show: Boolean) {
        _uiState.update { it.copy(showDeviceSheet = show) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        removeSurface()
        stopCamera()
    }
}
