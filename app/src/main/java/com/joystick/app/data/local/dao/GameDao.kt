package com.joystick.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joystick.app.data.local.entity.GameEntity

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE genre = :genre ORDER BY page ASC, rating DESC")
    suspend fun getGamesByGenre(genre: String): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE genre = :genre")
    suspend fun clearGamesByGenre(genre: String)

    @Query("SELECT cachedAt FROM games WHERE genre = :genre LIMIT 1")
    suspend fun getCacheTimestamp(genre: String): Long?
}
