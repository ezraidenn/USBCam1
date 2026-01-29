package com.biostar.camtest.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.biostar.camtest.data.models.GridType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val DEFAULT_GRID_TYPE = stringPreferencesKey("default_grid_type")
        val VIDEO_QUALITY = stringPreferencesKey("video_quality")
        val AUDIO_BITRATE = intPreferencesKey("audio_bitrate")
        val AUDIO_SAMPLE_RATE = intPreferencesKey("audio_sample_rate")
        val GRID_ENABLED_BY_DEFAULT = booleanPreferencesKey("grid_enabled_by_default")
        val AUTO_SAVE_LOCATION = stringPreferencesKey("auto_save_location")
    }

    // Grid preferences
    val defaultGridType: Flow<GridType> = dataStore.data.map { preferences ->
        val gridTypeString = preferences[DEFAULT_GRID_TYPE] ?: GridType.RULE_OF_THIRDS.name
        GridType.valueOf(gridTypeString)
    }

    suspend fun setDefaultGridType(gridType: GridType) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_GRID_TYPE] = gridType.name
        }
    }

    val gridEnabledByDefault: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[GRID_ENABLED_BY_DEFAULT] ?: false
    }

    suspend fun setGridEnabledByDefault(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[GRID_ENABLED_BY_DEFAULT] = enabled
        }
    }

    // Video quality preferences
    val videoQuality: Flow<String> = dataStore.data.map { preferences ->
        preferences[VIDEO_QUALITY] ?: "1080p"
    }

    suspend fun setVideoQuality(quality: String) {
        dataStore.edit { preferences ->
            preferences[VIDEO_QUALITY] = quality
        }
    }

    // Audio preferences
    val audioBitrate: Flow<Int> = dataStore.data.map { preferences ->
        preferences[AUDIO_BITRATE] ?: 128000 // 128 kbps
    }

    suspend fun setAudioBitrate(bitrate: Int) {
        dataStore.edit { preferences ->
            preferences[AUDIO_BITRATE] = bitrate
        }
    }

    val audioSampleRate: Flow<Int> = dataStore.data.map { preferences ->
        preferences[AUDIO_SAMPLE_RATE] ?: 44100 // 44.1 kHz
    }

    suspend fun setAudioSampleRate(sampleRate: Int) {
        dataStore.edit { preferences ->
            preferences[AUDIO_SAMPLE_RATE] = sampleRate
        }
    }

    // Storage preferences
    val autoSaveLocation: Flow<String> = dataStore.data.map { preferences ->
        preferences[AUTO_SAVE_LOCATION] ?: "Pictures/USBCam"
    }

    suspend fun setAutoSaveLocation(location: String) {
        dataStore.edit { preferences ->
            preferences[AUTO_SAVE_LOCATION] = location
        }
    }
}
