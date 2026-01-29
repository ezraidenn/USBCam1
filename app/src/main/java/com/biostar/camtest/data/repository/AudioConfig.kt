package com.biostar.camtest.data.repository

data class AudioConfig(
    val bitrate: Int = 128000, // 128 kbps default
    val sampleRate: Int = 44100, // 44.1 kHz default
    val channels: Int = 2 // Stereo
) {
    companion object {
        val BITRATE_64K = 64000
        val BITRATE_128K = 128000
        val BITRATE_192K = 192000
        val BITRATE_256K = 256000
        
        val SAMPLE_RATE_44_1K = 44100
        val SAMPLE_RATE_48K = 48000
    }
}
