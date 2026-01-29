package com.biostar.camtest.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.biostar.camtest.data.models.CapturedMedia
import com.biostar.camtest.data.models.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _capturedMedia = MutableStateFlow<List<CapturedMedia>>(emptyList())
    val capturedMedia: Flow<List<CapturedMedia>> = _capturedMedia.asStateFlow()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
    
    suspend fun savePhoto(bitmap: Bitmap, resolution: String): Result<CapturedMedia> = withContext(Dispatchers.IO) {
        try {
            val name = "USBCAM_${dateFormat.format(System.currentTimeMillis())}"
            
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/USB-CAM")
                }
            }
            
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            
            uri?.let {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                }
                
                val media = CapturedMedia(
                    id = System.currentTimeMillis(),
                    filePath = uri.toString(),
                    fileName = "$name.jpg",
                    timestamp = System.currentTimeMillis(),
                    type = MediaType.PHOTO,
                    resolution = resolution,
                    fileSize = 0L
                )
                
                _capturedMedia.value = _capturedMedia.value + media
                
                Timber.d("Photo saved: ${media.fileName}")
                Result.success(media)
            } ?: Result.failure(Exception("Failed to create media URI"))
            
        } catch (e: Exception) {
            Timber.e(e, "Error saving photo")
            Result.failure(e)
        }
    }
    
    suspend fun loadCapturedMedia() = withContext(Dispatchers.IO) {
        try {
            val mediaList = mutableListOf<CapturedMedia>()
            
            // Query images
            val imageProjection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATA
            )
            
            val imageSelection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
            } else {
                "${MediaStore.Images.Media.DATA} LIKE ?"
            }
            
            val imageSelectionArgs = arrayOf("%USB-CAM%")
            
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                imageSelection,
                imageSelectionArgs,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val date = cursor.getLong(dateColumn)
                    val size = cursor.getLong(sizeColumn)
                    val data = cursor.getString(dataColumn)
                    
                    mediaList.add(
                        CapturedMedia(
                            id = id,
                            filePath = data,
                            fileName = name,
                            timestamp = date * 1000,
                            type = MediaType.PHOTO,
                            resolution = "",
                            fileSize = size
                        )
                    )
                }
            }
            
            _capturedMedia.value = mediaList
            Timber.d("Loaded ${mediaList.size} media items")
            
        } catch (e: Exception) {
            Timber.e(e, "Error loading media")
        }
    }
    
    suspend fun deleteMedia(media: CapturedMedia): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(media.filePath)
            if (file.exists()) {
                file.delete()
            }
            
            _capturedMedia.value = _capturedMedia.value.filter { it.id != media.id }
            
            Timber.d("Media deleted: ${media.fileName}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting media")
            Result.failure(e)
        }
    }
}
