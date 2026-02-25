package com.joystick.app.domain.model

/**
 * Domain model representing a paginated page of games.
 * Encapsulates pagination metadata so upper layers don't need to know about raw API response shapes.
 */
data class GamesPage(
    val games: List<Game>,
    val hasNextPage: Boolean,
    val totalCount: Int
)
