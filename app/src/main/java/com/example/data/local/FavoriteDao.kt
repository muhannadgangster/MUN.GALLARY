package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
  @Query("SELECT * FROM favorites ORDER BY dateFavorited DESC")
  fun getAllFavorites(): Flow<List<FavoriteEntity>>

  @Query("SELECT mediaId FROM favorites")
  fun getAllFavoriteIds(): Flow<List<Long>>

  @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaId = :mediaId)")
  fun isFavorite(mediaId: Long): Flow<Boolean>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFavorite(favorite: FavoriteEntity)

  @Query("DELETE FROM favorites WHERE mediaId = :mediaId")
  suspend fun deleteFavorite(mediaId: Long)

  @Query("DELETE FROM favorites WHERE mediaId IN (:mediaIds)")
  suspend fun deleteFavorites(mediaIds: List<Long>)
}
