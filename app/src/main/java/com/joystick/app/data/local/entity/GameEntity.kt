package com.joystick.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double,
    val metacritic: Int?,
    val released: String?,
    val genre: String,
    val page: Int,
    val cachedAt: Long
)
