package com.biostar.camtest.data.models

import android.hardware.usb.UsbDevice

data class CameraResolution(
    val width: Int,
    val height: Int,
    val fps: Int
) {
    override fun toString(): String = "${width}x${height}@${fps}fps"
    
    val displayName: String
        get() = when {
            width >= 3840 -> "4K ${fps}fps"
            width >= 1920 -> "1080p ${fps}fps"
            width >= 1280 -> "720p ${fps}fps"
            else -> "${width}x${height} ${fps}fps"
        }
}

data class UsbCameraDevice(
    val device: UsbDevice,
    val name: String,
    val availableResolutions: List<CameraResolution>
) {
    val id: Int get() = device.deviceId
    val displayName: String get() = name.ifEmpty { "USB Camera ${device.deviceId}" }
}

data class CapturedMedia(
    val id: Long,
    val filePath: String,
    val fileName: String,
    val timestamp: Long,
    val type: MediaType,
    val resolution: String,
    val fileSize: Long,
    val duration: Long? = null
)

enum class MediaType {
    PHOTO,
    VIDEO
}

sealed class CameraState {
    object Idle : CameraState()
    object Connecting : CameraState()
    data class Connected(val device: UsbCameraDevice, val resolution: CameraResolution) : CameraState()
    data class Recording(val device: UsbCameraDevice, val duration: Long) : CameraState()
    data class Error(val message: String) : CameraState()
}

data class GridOverlay(
    val enabled: Boolean = false,
    val type: GridType = GridType.RULE_OF_THIRDS
)

enum class GridType {
    RULE_OF_THIRDS,
    SQUARE,
    GOLDEN_RATIO
}
