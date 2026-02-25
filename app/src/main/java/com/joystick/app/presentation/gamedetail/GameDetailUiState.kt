package com.joystick.app.presentation.gamedetail

import com.joystick.app.domain.model.GameDetail

sealed interface GameDetailUiState {
    object Loading : GameDetailUiState
    data class Success(val game: GameDetail) : GameDetailUiState
    data class Error(val message: String) : GameDetailUiState
}
