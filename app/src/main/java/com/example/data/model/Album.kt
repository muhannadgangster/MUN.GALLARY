package com.example.data.model

import android.net.Uri

data class Album(
  val id: String,
  val name: String,
  val coverUri: Uri?,
  val itemCount: Int,
  val photoCount: Int = 0,
  val videoCount: Int = 0,
  val lastModified: Long = 0L
)
