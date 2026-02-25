package com.joystick.app.domain.model

/**
 * Domain model representing the full detail of a single game.
 * Pure Kotlin — no Android or framework dependencies.
 */
data class GameDetail(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val imageUrlAdditional: String?,
    val description: String?,
    val released: String?,
    val rating: Double,
    val metacritic: Int?,
    val website: String?,
    val playtime: Int,
    val isTba: Boolean
)
