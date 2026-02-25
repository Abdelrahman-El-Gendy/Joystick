package com.joystick.app.presentation.gamelist

import com.joystick.app.domain.model.Game

/**
 * Sealed interface representing all possible UI states for the Game List screen.
 * Using sealed interface for exhaustive `when` handling in Compose.
 */
sealed interface GameListUiState {
    data object Loading : GameListUiState

    data class Success(
        val games: List<Game>,
        val selectedGenre: String?,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = true
    ) : GameListUiState

    data class Error(val message: String) : GameListUiState
}
