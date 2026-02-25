package com.joystick.app.domain.model

/**
 * Domain model representing a game in a list context.
 * Pure Kotlin — no Android or framework dependencies.
 */
data class Game(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double,
    val metacritic: Int?,
    val released: String?
)
