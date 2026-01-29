package com.biostar.camtest.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biostar.camtest.data.models.CapturedMedia
import com.biostar.camtest.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _mediaList = MutableStateFlow<List<CapturedMedia>>(emptyList())
    val mediaList: StateFlow<List<CapturedMedia>> = _mediaList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeMedia()
    }

    private fun observeMedia() {
        viewModelScope.launch {
            mediaRepository.capturedMedia.collect { media ->
                _mediaList.value = media
            }
        }
    }

    fun loadMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                mediaRepository.loadCapturedMedia()
            } catch (e: Exception) {
                Timber.e(e, "Error loading media")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMedia(media: CapturedMedia) {
        viewModelScope.launch {
            try {
                mediaRepository.deleteMedia(media)
                Timber.d("Media deleted: ${media.fileName}")
            } catch (e: Exception) {
                Timber.e(e, "Error deleting media")
            }
        }
    }
}
