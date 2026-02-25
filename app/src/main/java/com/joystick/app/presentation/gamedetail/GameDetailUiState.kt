package com.joystick.app.presentation.gamedetail

import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.model.Trailer
import com.joystick.app.domain.model.Screenshot

sealed interface GameDetailUiState {
    object Loading : GameDetailUiState
    data class Success(
        val game: GameDetail,
        val trailers: List<Trailer> = emptyList(),
        val screenshots: List<Screenshot> = emptyList(),
        val isLoadingExtras: Boolean = true
    ) : GameDetailUiState
    data class Error(val message: String) : GameDetailUiState
}
