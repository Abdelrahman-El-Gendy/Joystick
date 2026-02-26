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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase
) : ViewModel() {

    var genre: String = DEFAULT_GENRE
        private set

    val availableGenres = listOf(
        "action", "indie", "adventure", "rpg", "strategy", "shooter",
        "casual", "simulation", "puzzle", "arcade", "platformer",
        "racing", "sports", "fighting", "family"
    )

    private val _uiState = MutableStateFlow<GameListUiState>(GameListUiState.InitialLoading)
    val uiState: StateFlow<GameListUiState> = _uiState.asStateFlow()

    private var currentPage: Int = 1
    private var hasNextPage: Boolean = true
    private val paginationMutex = Mutex()

    private val allGames = mutableListOf<Game>()
    private var searchQuery = ""

    init {
        loadGames()
    }

    fun loadGames() {
        currentPage = 1
        hasNextPage = true
        searchQuery = ""
        allGames.clear()

        _uiState.value = GameListUiState.InitialLoading

        viewModelScope.launch {
            getGamesUseCase(genre = genre, page = 1).fold(
                onSuccess = { page ->
                    hasNextPage = page.hasNextPage
                    allGames.addAll(page.games)

                    if (allGames.isEmpty()) {
                        _uiState.value = GameListUiState.Empty(
                            reason = EmptyReason.NO_GENRE_RESULTS,
                            selectedGenre = genre,
                            searchQuery = searchQuery
                        )
                    } else {
                        _uiState.value = GameListUiState.Success(
                            allGames = allGames.toList(),
                            filteredGames = allGames.toList(),
                            hasNextPage = hasNextPage,
                            selectedGenre = genre
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.value = GameListUiState.Error(
                        message = error.localizedMessage ?: "Failed to load games"
                    )
                }
            )
        }
    }

    fun loadNextPage() {
        if (!hasNextPage) return

        val currentState = _uiState.value
        if (currentState !is GameListUiState.Success) return

        viewModelScope.launch {
            // Mutex prevents concurrent pagination calls
            if (!paginationMutex.tryLock()) return@launch

            try {
                _uiState.value = currentState.copy(isLoadingNextPage = true)
                currentPage++

                getGamesUseCase(genre = genre, page = currentPage).fold(
                    onSuccess = { page ->
                        allGames.addAll(page.games)
                        hasNextPage = page.hasNextPage

                        val filtered = applySearchFilter(allGames, searchQuery)

                        _uiState.value = GameListUiState.Success(
                            allGames = allGames.toList(),
                            filteredGames = filtered,
                            isLoadingNextPage = false,
                            searchQuery = searchQuery,
                            hasNextPage = hasNextPage,
                            selectedGenre = genre
                        )
                    },
                    onFailure = { error ->
                        currentPage--
                        _uiState.value = currentState.copy(
                            isLoadingNextPage = false,
                            paginationError = error.localizedMessage
                                ?: "Failed to load more games"
                        )
                    }
                )
            } finally {
                paginationMutex.unlock()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value
        if (currentState !is GameListUiState.Success && currentState !is GameListUiState.Empty) return

        searchQuery = query

        if (allGames.isEmpty()) return

        val filtered = applySearchFilter(allGames, query)

        if (filtered.isEmpty() && query.isNotEmpty()) {
            _uiState.value = GameListUiState.Empty(
                reason = EmptyReason.NO_SEARCH_RESULTS,
                selectedGenre = genre,
                searchQuery = query
            )
        } else if (query.isEmpty() && currentState is GameListUiState.Empty) {
            _uiState.value = GameListUiState.Success(
                allGames = allGames.toList(),
                filteredGames = allGames.toList(),
                isLoadingNextPage = false,
                searchQuery = query,
                hasNextPage = hasNextPage,
                selectedGenre = genre
            )
        } else {
            _uiState.value = GameListUiState.Success(
                allGames = allGames.toList(),
                filteredGames = filtered,
                isLoadingNextPage = false,
                searchQuery = query,
                hasNextPage = hasNextPage,
                selectedGenre = genre
            )
        }
    }

    fun onGenreSelected(newGenre: String) {
        if (genre == newGenre) return
        genre = newGenre
        loadGames()
    }

    fun retry() {
        loadGames()
    }

    fun clearPaginationError() {
        val currentState = _uiState.value
        if (currentState is GameListUiState.Success) {
            _uiState.value = currentState.copy(paginationError = null)
        }
    }

    private fun applySearchFilter(games: List<Game>, query: String): List<Game> {
        return if (query.isBlank()) {
            games
        } else {
            games.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    companion object {
        private const val DEFAULT_GENRE = "action"
    }
}
