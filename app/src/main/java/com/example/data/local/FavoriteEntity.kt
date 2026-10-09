package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
  @PrimaryKey val mediaId: Long,
  val mediaUri: String,
  val dateFavorited: Long = System.currentTimeMillis()
)
