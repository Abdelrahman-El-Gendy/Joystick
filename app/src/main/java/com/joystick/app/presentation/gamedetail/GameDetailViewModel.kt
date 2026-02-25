package com.joystick.app.presentation.gamedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joystick.app.domain.usecase.GetGameDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gameId: Int = savedStateHandle["gameId"] ?: error("gameId required")

    private val _uiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    init {
        loadGameDetail(gameId)
    }

    private fun loadGameDetail(id: Int) {
        _uiState.value = GameDetailUiState.Loading
        viewModelScope.launch {
            getGameDetailUseCase(id).fold(
                onSuccess = { detail ->
                    _uiState.value = GameDetailUiState.Success(detail)
                },
                onFailure = { error ->
                    _uiState.value = GameDetailUiState.Error(
                        message = error.localizedMessage ?: "Failed to load game details"
                    )
                }
            )
        }
    }

    fun retry() {
        loadGameDetail(gameId)
    }
}
