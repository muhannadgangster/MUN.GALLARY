package com.example.data.repository

import android.app.Activity
import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import com.example.data.model.Album
import com.example.data.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaStoreRepository(private val context: Context) {
  private val contentResolver: ContentResolver = context.contentResolver

  suspend fun fetchAllMedia(
    includeVideos: Boolean = true,
    includeScreenshots: Boolean = true
  ): List<MediaItem> = withContext(Dispatchers.IO) {
    val items = mutableListOf<MediaItem>()

    // Fetch images
    items.addAll(fetchImages(includeScreenshots))

    // Fetch videos
    if (includeVideos) {
      items.addAll(fetchVideos())
    }

    // Sort by date added descending by default
    items.sortedByDescending { it.dateAdded }
  }

  private fun fetchImages(includeScreenshots: Boolean): List<MediaItem> {
    val images = mutableListOf<MediaItem>()
    val projection = arrayOf(
      MediaStore.Images.Media._ID,
      MediaStore.Images.Media.DISPLAY_NAME,
      MediaStore.Images.Media.DATA,
      MediaStore.Images.Media.MIME_TYPE,
      MediaStore.Images.Media.SIZE,
      MediaStore.Images.Media.DATE_ADDED,
      MediaStore.Images.Media.DATE_MODIFIED,
      MediaStore.Images.Media.WIDTH,
      MediaStore.Images.Media.HEIGHT,
      MediaStore.Images.Media.BUCKET_ID,
      MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val selection = if (!includeScreenshots) {
      "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} NOT LIKE ? AND ${MediaStore.Images.Media.DISPLAY_NAME} NOT LIKE ?"
    } else null

    val selectionArgs = if (!includeScreenshots) {
      arrayOf("%Screenshot%", "%screenshot%")
    } else null

    runCatching {
      contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
      )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
        val dataCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        val mimeCol = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
        val sizeCol = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
        val dateAddedCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
        val dateModCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
        val widthCol = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
        val heightCol = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
        val bucketIdCol = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)
        val bucketNameCol = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
          val id = cursor.getLong(idCol)
          val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
          val name = if (nameCol != -1) cursor.getString(nameCol) ?: "Image_$id" else "Image_$id"
          val path = if (dataCol != -1) cursor.getString(dataCol) ?: "" else ""
          val mime = if (mimeCol != -1) cursor.getString(mimeCol) ?: "image/jpeg" else "image/jpeg"
          val size = if (sizeCol != -1) cursor.getLong(sizeCol) else 0L
          val dateAdded = if (dateAddedCol != -1) cursor.getLong(dateAddedCol) else 0L
          val dateModified = if (dateModCol != -1) cursor.getLong(dateModCol) else 0L
          val width = if (widthCol != -1) cursor.getInt(widthCol) else 0
          val height = if (heightCol != -1) cursor.getInt(heightCol) else 0
          val bucketId = if (bucketIdCol != -1) cursor.getString(bucketIdCol) ?: "" else ""
          val bucketName = if (bucketNameCol != -1) cursor.getString(bucketNameCol) ?: "Internal" else "Internal"

          images.add(
            MediaItem(
              id = id,
              uri = uri,
              name = name,
              path = path,
              mimeType = mime,
              size = size,
              dateAdded = dateAdded,
              dateModified = dateModified,
              duration = 0L,
              width = width,
              height = height,
              isVideo = false,
              bucketId = bucketId,
              bucketName = bucketName
            )
          )
        }
      }
    }

    return images
  }

  private fun fetchVideos(): List<MediaItem> {
    val videos = mutableListOf<MediaItem>()
    val projection = arrayOf(
      MediaStore.Video.Media._ID,
      MediaStore.Video.Media.DISPLAY_NAME,
      MediaStore.Video.Media.DATA,
      MediaStore.Video.Media.MIME_TYPE,
      MediaStore.Video.Media.SIZE,
      MediaStore.Video.Media.DATE_ADDED,
      MediaStore.Video.Media.DATE_MODIFIED,
      MediaStore.Video.Media.DURATION,
      MediaStore.Video.Media.WIDTH,
      MediaStore.Video.Media.HEIGHT,
      MediaStore.Video.Media.BUCKET_ID,
      MediaStore.Video.Media.BUCKET_DISPLAY_NAME
    )

    val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

    runCatching {
      contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
      )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameCol = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
        val dataCol = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
        val mimeCol = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)
        val sizeCol = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
        val dateAddedCol = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
        val dateModCol = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)
        val durationCol = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
        val widthCol = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
        val heightCol = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
        val bucketIdCol = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
        val bucketNameCol = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
          val id = cursor.getLong(idCol)
          val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
          val name = if (nameCol != -1) cursor.getString(nameCol) ?: "Video_$id" else "Video_$id"
          val path = if (dataCol != -1) cursor.getString(dataCol) ?: "" else ""
          val mime = if (mimeCol != -1) cursor.getString(mimeCol) ?: "video/mp4" else "video/mp4"
          val size = if (sizeCol != -1) cursor.getLong(sizeCol) else 0L
          val dateAdded = if (dateAddedCol != -1) cursor.getLong(dateAddedCol) else 0L
          val dateModified = if (dateModCol != -1) cursor.getLong(dateModCol) else 0L
          val duration = if (durationCol != -1) cursor.getLong(durationCol) else 0L
          val width = if (widthCol != -1) cursor.getInt(widthCol) else 0
          val height = if (heightCol != -1) cursor.getInt(heightCol) else 0
          val bucketId = if (bucketIdCol != -1) cursor.getString(bucketIdCol) ?: "" else ""
          val bucketName = if (bucketNameCol != -1) cursor.getString(bucketNameCol) ?: "Internal" else "Internal"

          videos.add(
            MediaItem(
              id = id,
              uri = uri,
              name = name,
              path = path,
              mimeType = mime,
              size = size,
              dateAdded = dateAdded,
              dateModified = dateModified,
              duration = duration,
              width = width,
              height = height,
              isVideo = true,
              bucketId = bucketId,
              bucketName = bucketName
            )
          )
        }
      }
    }

    return videos
  }

  suspend fun fetchAlbums(mediaList: List<MediaItem>): List<Album> = withContext(Dispatchers.Default) {
    val albumMap = mutableMapOf<String, MutableList<MediaItem>>()

    for (item in mediaList) {
      val key = if (item.bucketId.isNotBlank()) item.bucketId else item.bucketName
      albumMap.getOrPut(key) { mutableListOf() }.add(item)
    }

    albumMap.map { (id, items) ->
      val firstItem = items.firstOrNull()
      val name = firstItem?.bucketName?.ifBlank { "Photos" } ?: "Album"
      val photos = items.count { !it.isVideo }
      val videos = items.count { it.isVideo }
      Album(
        id = id,
        name = name,
        coverUri = firstItem?.uri,
        itemCount = items.size,
        photoCount = photos,
        videoCount = videos,
        lastModified = items.maxOfOrNull { it.dateModified } ?: 0L
      )
    }.sortedByDescending { it.itemCount }
  }

  sealed class DeleteResult {
    object Success : DeleteResult()
    data class RequiresPermission(val intentSenderRequest: IntentSenderRequest) : DeleteResult()
    data class Failure(val error: String) : DeleteResult()
  }

  suspend fun deleteMediaItems(items: List<MediaItem>): DeleteResult = withContext(Dispatchers.IO) {
    if (items.isEmpty()) return@withContext DeleteResult.Success

    val uris = items.map { it.uri }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      try {
        val pendingIntent = MediaStore.createDeleteRequest(contentResolver, uris)
        val request = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
        return@withContext DeleteResult.RequiresPermission(request)
      } catch (e: Exception) {
        return@withContext DeleteResult.Failure(e.localizedMessage ?: "Failed to create delete request")
      }
    } else {
      var failed = 0
      for (item in items) {
        try {
          val deletedRows = contentResolver.delete(item.uri, null, null)
          if (deletedRows <= 0) failed++
        } catch (e: SecurityException) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
            val intentSender = e.userAction.actionIntent.intentSender
            val request = IntentSenderRequest.Builder(intentSender).build()
            return@withContext DeleteResult.RequiresPermission(request)
          } else {
            failed++
          }
        } catch (e: Exception) {
          failed++
        }
      }
      return@withContext if (failed == 0) DeleteResult.Success else DeleteResult.Failure("Could not delete $failed items")
    }
  }

  suspend fun saveEditedImageCopy(
    bitmap: Bitmap,
    originalName: String
  ): Uri? = withContext(Dispatchers.IO) {
    try {
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val baseName = originalName.substringBeforeLast(".")
      val newDisplayName = "MUN_EDIT_${baseName}_$timeStamp.jpg"

      val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, newDisplayName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/M-U-N Gallery")
          put(MediaStore.Images.Media.IS_PENDING, 1)
        }
      }

      val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
      if (uri != null) {
        contentResolver.openOutputStream(uri)?.use { stream ->
          bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          values.clear()
          values.put(MediaStore.Images.Media.IS_PENDING, 0)
          contentResolver.update(uri, values, null, null)
        }
        return@withContext uri
      }
      null
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun createShareIntent(items: List<MediaItem>): Intent {
    return if (items.size == 1) {
      val item = items[0]
      Intent(Intent.ACTION_SEND).apply {
        type = item.mimeType
        putExtra(Intent.EXTRA_STREAM, item.uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
    } else {
      val uris = ArrayList(items.map { it.uri })
      val isAllImages = items.all { !it.isVideo }
      val isAllVideos = items.all { it.isVideo }
      val mimeType = when {
        isAllImages -> "image/*"
        isAllVideos -> "video/*"
        else -> "*/*"
      }
      Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = mimeType
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
    }
  }
}
