package com.joystick.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Each object/class is a KEY on the back stack
// Use @Serializable for type safety

@Serializable
sealed interface NavRoute : NavKey

@Serializable
data object GameListRoute : NavRoute

@Serializable
data class GameDetailRoute(
    val gameId: Int
) : NavRoute
