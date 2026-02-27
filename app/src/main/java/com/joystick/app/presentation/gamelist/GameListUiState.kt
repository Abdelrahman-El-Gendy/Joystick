package com.joystick.app.presentation.gamelist

import com.joystick.app.domain.model.Game

sealed interface GameListUiState {

    object InitialLoading : GameListUiState

    /**
     * Emitted when the user switches genres (or retries after an error).
     * The genre row and search bar remain visible while only the content list shimmers.
     */
    data class GenreLoading(
        val selectedGenre: String?,
        val searchQuery: String = ""
    ) : GameListUiState

    data class Success(
        val allGames: List<Game>,
        val filteredGames: List<Game>,
        val isLoadingNextPage: Boolean = false,
        val searchQuery: String = "",
        val hasNextPage: Boolean = true,
        val paginationError: String? = null,
        val selectedGenre: String? = null
    ) : GameListUiState

    data class Error(val message: String) : GameListUiState

    data class Empty(
        val reason: EmptyReason,
        val selectedGenre: String? = null,
        val searchQuery: String = ""
    ) : GameListUiState
}

enum class EmptyReason {
    NO_GENRE_RESULTS,
    NO_SEARCH_RESULTS
}
