package com.joystick.app.presentation.gamedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joystick.app.domain.model.Trailer
import com.joystick.app.domain.usecase.GetGameDetailUseCase
import com.joystick.app.domain.usecase.GetGameScreenshotsUseCase
import com.joystick.app.domain.usecase.GetGameTrailersUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = GameDetailViewModel.Factory::class)
class GameDetailViewModel @AssistedInject constructor(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    private val getGameTrailersUseCase: GetGameTrailersUseCase,
    private val getGameScreenshotsUseCase: GetGameScreenshotsUseCase,
    @Assisted private val gameId: Int
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(gameId: Int): GameDetailViewModel
    }

    private val _uiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    private val _selectedTrailer = MutableStateFlow<Trailer?>(null)
    val selectedTrailer: StateFlow<Trailer?> = _selectedTrailer.asStateFlow()

    init {
        loadGameDetail()
    }

    private fun loadGameDetail() {
        viewModelScope.launch {
            _uiState.value = GameDetailUiState.Loading

            getGameDetailUseCase(gameId)
                .onSuccess { game ->
                    _uiState.value = GameDetailUiState.Success(
                        game = game,
                        isLoadingExtras = true
                    )
                    loadExtras()
                }
                .onFailure { error ->
                    _uiState.value = GameDetailUiState.Error(
                        error.localizedMessage ?: "Unknown error"
                    )
                }
        }
    }

    private fun loadExtras() {
        viewModelScope.launch {
            val trailersDeferred = async { getGameTrailersUseCase(gameId) }
            val screenshotsDeferred = async { getGameScreenshotsUseCase(gameId) }

            val trailers = trailersDeferred.await().getOrElse { emptyList() }
            val screenshots = screenshotsDeferred.await().getOrElse { emptyList() }

            val current = _uiState.value
            if (current is GameDetailUiState.Success) {
                _uiState.value = current.copy(
                    trailers = trailers,
                    screenshots = screenshots,
                    isLoadingExtras = false
                )
            }
        }
    }

    fun onTrailerSelected(trailer: Trailer) {
        _selectedTrailer.value = trailer
    }

    fun onTrailerDismissed() {
        _selectedTrailer.value = null
    }

    fun retry() {
        loadGameDetail()
    }
}
