package com.joystick.app.presentation.gamelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joystick.app.domain.model.Game
import com.joystick.app.domain.usecase.GetGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameListUiState>(GameListUiState.Loading)
    val uiState: StateFlow<GameListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var selectedGenre: String? = null
    private var allGames = mutableListOf<Game>()

    /** Hardcoded genre list — avoids an extra API call for the filter chips. */
    val availableGenres = listOf(
        "action", "adventure", "rpg", "strategy", "shooter",
        "puzzle", "racing", "sports", "simulation", "platformer",
        "fighting", "indie", "casual", "arcade", "family"
    )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = GameListUiState.Loading
            val genre = selectedGenre ?: availableGenres.first()
            getGamesUseCase(genre = genre, page = 1).fold(
                onSuccess = { page ->
                    currentPage = 1
                    allGames.clear()
                    allGames.addAll(page.games)
                    _uiState.value = GameListUiState.Success(
                        games = allGames.toList(),
                        selectedGenre = genre,
                        canLoadMore = page.hasNextPage
                    )
                },
                onFailure = { error ->
                    _uiState.value = GameListUiState.Error(
                        message = error.localizedMessage ?: "Failed to load games"
                    )
                }
            )
        }
    }

    fun onGenreSelected(genre: String?) {
        if (selectedGenre == genre) return
        selectedGenre = genre
        loadInitialData()
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState !is GameListUiState.Success) return
        if (currentState.isLoadingMore || !currentState.canLoadMore) return

        _uiState.value = currentState.copy(isLoadingMore = true)
        val genre = selectedGenre ?: availableGenres.first()

        viewModelScope.launch {
            getGamesUseCase(genre = genre, page = currentPage + 1).fold(
                onSuccess = { page ->
                    currentPage++
                    allGames.addAll(page.games)
                    _uiState.value = GameListUiState.Success(
                        games = allGames.toList(),
                        selectedGenre = selectedGenre,
                        isLoadingMore = false,
                        canLoadMore = page.hasNextPage
                    )
                },
                onFailure = {
                    _uiState.value = currentState.copy(isLoadingMore = false)
                }
            )
        }
    }

    fun retry() {
        loadInitialData()
    }
}
