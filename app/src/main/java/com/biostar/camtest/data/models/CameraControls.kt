package com.biostar.camtest.data.models

/**
 * UVC Camera Controls based on official UVC specification
 * Reference: USB Video Class specification and UVCCamera library
 */
data class CameraControlValue(
    val current: Int = 0,
    val min: Int = 0,
    val max: Int = 100,
    val def: Int = 50,
    val isSupported: Boolean = false
)

data class CameraControls(
    // Processing Unit Controls
    val brightness: CameraControlValue = CameraControlValue(),
    val contrast: CameraControlValue = CameraControlValue(),
    val hue: CameraControlValue = CameraControlValue(),
    val saturation: CameraControlValue = CameraControlValue(),
    val sharpness: CameraControlValue = CameraControlValue(),
    val gamma: CameraControlValue = CameraControlValue(),
    val whiteBalance: CameraControlValue = CameraControlValue(),
    val backlightCompensation: CameraControlValue = CameraControlValue(),
    val gain: CameraControlValue = CameraControlValue(),
    val powerLineFrequency: CameraControlValue = CameraControlValue(),
    
    // Camera Terminal Controls
    val exposureTime: CameraControlValue = CameraControlValue(),
    val focus: CameraControlValue = CameraControlValue(),
    val zoom: CameraControlValue = CameraControlValue(),
    
    // Auto modes
    val autoWhiteBalance: Boolean = true,
    val autoExposure: Boolean = true,
    val autoFocus: Boolean = true
)

/**
 * UVC Control IDs from official specification
 */
object UVCControlId {
    // Processing Unit Controls (PU)
    const val PU_BRIGHTNESS = 0x00000001
    const val PU_CONTRAST = 0x00000002
    const val PU_HUE = 0x00000004
    const val PU_SATURATION = 0x00000008
    const val PU_SHARPNESS = 0x00000010
    const val PU_GAMMA = 0x00000020
    const val PU_WHITE_BALANCE_TEMPERATURE = 0x00000040
    const val PU_WHITE_BALANCE_COMPONENT = 0x00000080
    const val PU_BACKLIGHT_COMPENSATION = 0x00000100
    const val PU_GAIN = 0x00000200
    const val PU_POWER_LINE_FREQUENCY = 0x00000400
    const val PU_HUE_AUTO = 0x00000800
    const val PU_WHITE_BALANCE_TEMPERATURE_AUTO = 0x00001000
    const val PU_WHITE_BALANCE_COMPONENT_AUTO = 0x00002000
    const val PU_DIGITAL_MULTIPLIER = 0x00004000
    const val PU_DIGITAL_MULTIPLIER_LIMIT = 0x00008000
    const val PU_ANALOG_VIDEO_STANDARD = 0x00010000
    const val PU_ANALOG_VIDEO_LOCK_STATUS = 0x00020000
    
    // Camera Terminal Controls (CT)
    const val CT_SCANNING_MODE = 0x00000001
    const val CT_AE_MODE = 0x00000002
    const val CT_AE_PRIORITY = 0x00000004
    const val CT_EXPOSURE_TIME_ABSOLUTE = 0x00000008
    const val CT_EXPOSURE_TIME_RELATIVE = 0x00000010
    const val CT_FOCUS_ABSOLUTE = 0x00000020
    const val CT_FOCUS_RELATIVE = 0x00000040
    const val CT_FOCUS_AUTO = 0x00000080
    const val CT_IRIS_ABSOLUTE = 0x00000100
    const val CT_IRIS_RELATIVE = 0x00000200
    const val CT_ZOOM_ABSOLUTE = 0x00000400
    const val CT_ZOOM_RELATIVE = 0x00000800
    const val CT_PANTILT_ABSOLUTE = 0x00001000
    const val CT_PANTILT_RELATIVE = 0x00002000
    const val CT_ROLL_ABSOLUTE = 0x00004000
    const val CT_ROLL_RELATIVE = 0x00008000
    const val CT_PRIVACY = 0x00010000
}
