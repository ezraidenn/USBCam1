package com.biostar.camtest.data.repository

import com.biostar.camtest.data.models.CameraControlValue
import com.biostar.camtest.data.models.CameraControls
import com.biostar.camtest.data.models.UVCControlId
import com.herohan.uvcapp.ICameraHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
 * Manager for UVC Camera Controls based on official UVC specification
 * Handles all Processing Unit and Camera Terminal controls
 */
class CameraControlsManager(private val cameraHelper: ICameraHelper) {
    
    private val _controls = MutableStateFlow(CameraControls())
    val controls: StateFlow<CameraControls> = _controls.asStateFlow()
    
    /**
     * Initialize and read all available camera controls
     */
    fun initializeControls() {
        try {
            val controls = CameraControls(
                brightness = readControl(UVCControlId.PU_BRIGHTNESS),
                contrast = readControl(UVCControlId.PU_CONTRAST),
                hue = readControl(UVCControlId.PU_HUE),
                saturation = readControl(UVCControlId.PU_SATURATION),
                sharpness = readControl(UVCControlId.PU_SHARPNESS),
                gamma = readControl(UVCControlId.PU_GAMMA),
                whiteBalance = readControl(UVCControlId.PU_WHITE_BALANCE_TEMPERATURE),
                backlightCompensation = readControl(UVCControlId.PU_BACKLIGHT_COMPENSATION),
                gain = readControl(UVCControlId.PU_GAIN),
                powerLineFrequency = readControl(UVCControlId.PU_POWER_LINE_FREQUENCY),
                exposureTime = readControl(UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE),
                focus = readControl(UVCControlId.CT_FOCUS_ABSOLUTE),
                zoom = readControl(UVCControlId.CT_ZOOM_ABSOLUTE),
                autoWhiteBalance = isAutoMode(UVCControlId.PU_WHITE_BALANCE_TEMPERATURE_AUTO),
                autoExposure = isAutoMode(UVCControlId.CT_AE_MODE),
                autoFocus = isAutoMode(UVCControlId.CT_FOCUS_AUTO)
            )
            _controls.value = controls
            Timber.d("Camera controls initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error initializing camera controls")
        }
    }
    
    /**
     * Read a specific control value from the camera
     */
    private fun readControl(controlId: Int): CameraControlValue {
        return try {
            // Check if control is supported
            val isSupported = cameraHelper.checkSupportFlag(controlId)
            if (!isSupported) {
                return CameraControlValue(isSupported = false)
            }
            
            // Get current value
            val current = cameraHelper.getValue(controlId)
            
            // Get min/max/default values
            // Note: UVCCamera API may not expose min/max directly
            // Using reasonable defaults based on UVC specification
            val (min, max, def) = getControlRange(controlId)
            
            CameraControlValue(
                current = current,
                min = min,
                max = max,
                def = def,
                isSupported = true
            )
        } catch (e: Exception) {
            Timber.w(e, "Error reading control $controlId")
            CameraControlValue(isSupported = false)
        }
    }
    
    /**
     * Get typical range for a control based on UVC specification
     */
    private fun getControlRange(controlId: Int): Triple<Int, Int, Int> {
        return when (controlId) {
            UVCControlId.PU_BRIGHTNESS -> Triple(-128, 127, 0)
            UVCControlId.PU_CONTRAST -> Triple(0, 255, 128)
            UVCControlId.PU_HUE -> Triple(-180, 180, 0)
            UVCControlId.PU_SATURATION -> Triple(0, 255, 128)
            UVCControlId.PU_SHARPNESS -> Triple(0, 255, 128)
            UVCControlId.PU_GAMMA -> Triple(1, 500, 100)
            UVCControlId.PU_WHITE_BALANCE_TEMPERATURE -> Triple(2800, 6500, 4000)
            UVCControlId.PU_BACKLIGHT_COMPENSATION -> Triple(0, 2, 1)
            UVCControlId.PU_GAIN -> Triple(0, 255, 128)
            UVCControlId.PU_POWER_LINE_FREQUENCY -> Triple(0, 2, 1) // 0=Disabled, 1=50Hz, 2=60Hz
            UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE -> Triple(1, 10000, 156)
            UVCControlId.CT_FOCUS_ABSOLUTE -> Triple(0, 255, 128)
            UVCControlId.CT_ZOOM_ABSOLUTE -> Triple(100, 400, 100)
            else -> Triple(0, 100, 50)
        }
    }
    
    /**
     * Check if an auto mode is enabled
     */
    private fun isAutoMode(controlId: Int): Boolean {
        return try {
            cameraHelper.checkSupportFlag(controlId) && 
            cameraHelper.getValue(controlId) == 1
        } catch (e: Exception) {
            true // Default to auto
        }
    }
    
    /**
     * Set a control value
     */
    fun setControlValue(controlId: Int, value: Int): Boolean {
        return try {
            cameraHelper.setValue(controlId, value)
            // Update local state
            initializeControls()
            Timber.d("Set control $controlId to $value")
            true
        } catch (e: Exception) {
            Timber.e(e, "Error setting control $controlId to $value")
            false
        }
    }
    
    /**
     * Reset a control to default value
     */
    fun resetControl(controlId: Int): Boolean {
        return try {
            cameraHelper.resetValue(controlId)
            initializeControls()
            Timber.d("Reset control $controlId")
            true
        } catch (e: Exception) {
            Timber.e(e, "Error resetting control $controlId")
            false
        }
    }
    
    /**
     * Reset all controls to defaults
     */
    fun resetAllControls() {
        val controlIds = listOf(
            UVCControlId.PU_BRIGHTNESS,
            UVCControlId.PU_CONTRAST,
            UVCControlId.PU_HUE,
            UVCControlId.PU_SATURATION,
            UVCControlId.PU_SHARPNESS,
            UVCControlId.PU_GAMMA,
            UVCControlId.PU_WHITE_BALANCE_TEMPERATURE,
            UVCControlId.PU_BACKLIGHT_COMPENSATION,
            UVCControlId.PU_GAIN,
            UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE,
            UVCControlId.CT_FOCUS_ABSOLUTE,
            UVCControlId.CT_ZOOM_ABSOLUTE
        )
        
        controlIds.forEach { controlId ->
            try {
                resetControl(controlId)
            } catch (e: Exception) {
                Timber.w(e, "Could not reset control $controlId")
            }
        }
    }
    
    /**
     * Set auto white balance
     */
    fun setAutoWhiteBalance(enabled: Boolean) {
        setControlValue(UVCControlId.PU_WHITE_BALANCE_TEMPERATURE_AUTO, if (enabled) 1 else 0)
    }
    
    /**
     * Set auto exposure
     */
    fun setAutoExposure(enabled: Boolean) {
        setControlValue(UVCControlId.CT_AE_MODE, if (enabled) 2 else 1) // 2=auto, 1=manual
    }
    
    /**
     * Set auto focus
     */
    fun setAutoFocus(enabled: Boolean) {
        setControlValue(UVCControlId.CT_FOCUS_AUTO, if (enabled) 1 else 0)
    }
}
