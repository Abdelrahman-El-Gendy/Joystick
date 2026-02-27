package com.joystick.app.presentation.gamelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joystick.app.domain.model.Game
import com.joystick.app.presentation.gamelist.components.*
import com.joystick.app.ui.components.ErrorStateView
import com.joystick.app.ui.components.JoystickScaffold
import com.joystick.app.ui.components.JoystickTopBar
import com.joystick.app.ui.theme.JoystickTheme

@Composable
fun GameListScreen(
    onGameClick: (Int) -> Unit,
    viewModel: GameListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameListScreenContent(
        uiState = uiState,
        availableGenres = viewModel.availableGenres,
        selectedGenreFromVm = viewModel.genre,
        onGameClick = onGameClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onGenreSelected = viewModel::onGenreSelected,
        onRetry = viewModel::retry,
        loadNextPage = viewModel::loadNextPage,
        clearPaginationError = viewModel::clearPaginationError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GameListScreenContent(
    uiState: GameListUiState,
    availableGenres: List<String>,
    selectedGenreFromVm: String,
    onGameClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onGenreSelected: (String) -> Unit,
    onRetry: () -> Unit,
    loadNextPage: () -> Unit,
    clearPaginationError: () -> Unit,
) {
    val horizontalScreenPadding = 20.dp
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()

    // Handle Pagination Error
    (uiState as? GameListUiState.Success)?.paginationError?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
            clearPaginationError()
        }
    }

    // Derive shared UI values from the current state
    val isInitialLoad = uiState is GameListUiState.InitialLoading
    val isGenreLoading = uiState is GameListUiState.GenreLoading

    val selectedGenre = when (uiState) {
        is GameListUiState.Success -> uiState.selectedGenre
        is GameListUiState.Empty -> uiState.selectedGenre
        is GameListUiState.GenreLoading -> uiState.selectedGenre
        else -> selectedGenreFromVm
    }

    val searchQuery = when (uiState) {
        is GameListUiState.Success -> uiState.searchQuery
        is GameListUiState.Empty -> uiState.searchQuery
        else -> ""
    }

    JoystickScaffold(
        topBar = { JoystickTopBar(title = "Joystick") },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // Genre row — always visible except during the very first load
            if (!isInitialLoad) {
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = horizontalScreenPadding,
                        vertical = 8.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableGenres) { g ->
                        FilterChip(
                            selected = g == selectedGenre,
                            // Disable chip taps while a genre is already loading
                            onClick = { if (!isGenreLoading) onGenreSelected(g) },
                            label = { Text(g.replaceFirstChar { it.uppercase() }) },
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
            }

            // Search bar — visible for Success, Empty, and GenreLoading; hidden for Error
            val showSearchBar = uiState is GameListUiState.Success ||
                    uiState is GameListUiState.Empty ||
                    uiState is GameListUiState.GenreLoading
            if (showSearchBar) {
                GameListSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    enabled = !isGenreLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalScreenPadding, vertical = 8.dp)
                )
            }

            when (val state = uiState) {
                is GameListUiState.InitialLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 12.dp
                        )
                    ) {
                        items(6) { GameCardShimmer() }
                    }
                }

                // Only shimmer the list — genre row and search bar remain visible above
                is GameListUiState.GenreLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 12.dp
                        )
                    ) {
                        items(6) { GameCardShimmer() }
                    }
                }

                is GameListUiState.Error -> {
                    ErrorStateView(
                        errorMessage = state.message,
                        onRetry = onRetry
                    )
                }

                is GameListUiState.Empty -> {
                    GameListEmptyView(
                        reason = state.reason,
                        onRetry = onRetry
                    )
                }

                is GameListUiState.Success -> {
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(state.filteredGames, key = { it.id }) { game ->
                            GameListItem(game = game, onGameClick = onGameClick)
                        }
                        if (state.isLoadingNextPage) {
                            item { PaginationFooter() }
                        }
                    }

                    // Infinite scroll trigger
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val lastItem =
                                lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
                            lastItem != null && lastItem.index >= state.filteredGames.size - 3
                        }
                    }
                    LaunchedEffect(shouldLoadMore.value) {
                        if (shouldLoadMore.value) loadNextPage()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameListScreenPreview() {
    val sampleGames = listOf(
        Game(
            1,
            "The Witcher 3: Wild Hunt",
            "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg",
            4.8,
            120,
            "Geralt of Rivia, a solitary monster hunter, struggles to find his place in a world where people often prove more wicked than beasts."
        ),
        Game(
            2,
            "Red Dead Redemption 2",
            "https://media.rawg.io/media/games/511/5118aff5091cb3efec399c808f8c598f.jpg",
            4.9,
            100,
            "America, 1899. The end of the wild west era has begun as lawmen hunt down the last remaining outlaw gangs. Those who will not surrender or succumb are killed."
        ),
        Game(
            3,
            "Portal 2",
            "https://media.rawg.io/media/games/328/3283617cb7d75d67257fc58339188742.jpg",
            4.7,
            90,
            "The Perpetual Testing Initiative has been expanded to allow you to design co-op puzzles for you and your friends!"
        ),
    )

    JoystickTheme {
        GameListScreenContent(
            uiState = GameListUiState.Success(
                allGames = sampleGames,
                filteredGames = sampleGames,
                selectedGenre = "action",
                searchQuery = "",
                hasNextPage = true,
                isLoadingNextPage = false,
                paginationError = null
            ),
            availableGenres = listOf("action", "adventure", "shooter"),
            selectedGenreFromVm = "action",
            onGameClick = {},
            onSearchQueryChanged = {},
            onGenreSelected = {},
            onRetry = {},
            loadNextPage = {},
            clearPaginationError = {}
        )
    }
}
