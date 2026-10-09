package com.example.data.model

import android.net.Uri

data class MediaItem(
  val id: Long,
  val uri: Uri,
  val name: String,
  val path: String,
  val mimeType: String,
  val size: Long,
  val dateAdded: Long,
  val dateModified: Long,
  val duration: Long = 0L,
  val width: Int = 0,
  val height: Int = 0,
  val isVideo: Boolean = false,
  val bucketId: String = "",
  val bucketName: String = "",
  val isFavorite: Boolean = false
) {
  val formattedSize: String
    get() {
      val kb = size / 1024.0
      val mb = kb / 1024.0
      val gb = mb / 1024.0
      return when {
        gb >= 1.0 -> String.format("%.2f GB", gb)
        mb >= 1.0 -> String.format("%.1f MB", mb)
        kb >= 1.0 -> String.format("%.1f KB", kb)
        else -> "$size B"
      }
    }

  val formattedDuration: String
    get() {
      if (!isVideo || duration <= 0) return ""
      val totalSeconds = duration / 1000
      val minutes = totalSeconds / 60
      val seconds = totalSeconds % 60
      val hours = minutes / 60
      return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
      } else {
        String.format("%02d:%02d", minutes, seconds)
      }
    }
}
