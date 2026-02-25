package com.joystick.app.presentation.gamedetail

import com.joystick.app.domain.model.GameDetail

/**
 * Sealed interface representing all possible UI states for the Game Detail screen.
 */
sealed interface GameDetailUiState {
    data object Loading : GameDetailUiState
    data class Success(val game: GameDetail) : GameDetailUiState
    data class Error(val message: String) : GameDetailUiState
}
