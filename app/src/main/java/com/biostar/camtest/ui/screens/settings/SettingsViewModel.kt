package com.biostar.camtest.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biostar.camtest.data.models.GridType
import com.biostar.camtest.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val defaultGridType: GridType = GridType.RULE_OF_THIRDS,
    val gridEnabledByDefault: Boolean = false,
    val videoQuality: String = "1080p",
    val audioBitrate: Int = 128000,
    val audioSampleRate: Int = 44100,
    val autoSaveLocation: String = "Pictures/USBCam"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            combine(
                userPreferences.defaultGridType,
                userPreferences.gridEnabledByDefault,
                userPreferences.videoQuality,
                userPreferences.audioBitrate,
                userPreferences.audioSampleRate,
                userPreferences.autoSaveLocation
            ) { gridType, gridEnabled, quality, bitrate, sampleRate, location ->
                SettingsUiState(
                    defaultGridType = gridType,
                    gridEnabledByDefault = gridEnabled,
                    videoQuality = quality,
                    audioBitrate = bitrate,
                    audioSampleRate = sampleRate,
                    autoSaveLocation = location
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setDefaultGridType(gridType: GridType) {
        viewModelScope.launch {
            userPreferences.setDefaultGridType(gridType)
        }
    }

    fun setGridEnabledByDefault(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setGridEnabledByDefault(enabled)
        }
    }

    fun setVideoQuality(quality: String) {
        viewModelScope.launch {
            userPreferences.setVideoQuality(quality)
        }
    }

    fun setAudioBitrate(bitrate: Int) {
        viewModelScope.launch {
            userPreferences.setAudioBitrate(bitrate)
        }
    }

    fun setAudioSampleRate(sampleRate: Int) {
        viewModelScope.launch {
            userPreferences.setAudioSampleRate(sampleRate)
        }
    }

    fun setAutoSaveLocation(location: String) {
        viewModelScope.launch {
            userPreferences.setAutoSaveLocation(location)
        }
    }
}
