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
import kotlinx.coroutines.async
import com.joystick.app.domain.usecase.GetGameTrailersUseCase
import com.joystick.app.domain.usecase.GetGameScreenshotsUseCase
import com.joystick.app.domain.model.Trailer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

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

    init {
        loadGameDetail(gameId)
    }

    private fun loadGameDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = GameDetailUiState.Loading

            // Load core details first
            getGameDetailUseCase(id)
                .onSuccess { game ->
                    _uiState.value = GameDetailUiState.Success(
                        game = game,
                        isLoadingExtras = true  // signal extras still loading
                    )
                    // Load trailers + screenshots in parallel after core details
                    loadExtras(id)
                }
                .onFailure { error ->
                    _uiState.value = GameDetailUiState.Error(
                        error.localizedMessage ?: "Unknown error"
                    )
                }
        }
    }

    private fun loadExtras(id: Int) {
        viewModelScope.launch {
            // Launch both in parallel
            val trailersDeferred = async { getGameTrailersUseCase(id) }
            val screenshotsDeferred = async { getGameScreenshotsUseCase(id) }

            val trailers = trailersDeferred.await().getOrElse { emptyList() }
            val screenshots = screenshotsDeferred.await().getOrElse { emptyList() }

            // Update Success state with extras — silently ignore failures
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

    // Track selected trailer for playback
    private val _selectedTrailer = MutableStateFlow<Trailer?>(null)
    val selectedTrailer: StateFlow<Trailer?> = _selectedTrailer.asStateFlow()

    fun onTrailerSelected(trailer: Trailer) {
        _selectedTrailer.value = trailer
    }

    fun onTrailerDismissed() {
        _selectedTrailer.value = null
    }

    fun retry() {
        loadGameDetail(gameId)
    }
}
