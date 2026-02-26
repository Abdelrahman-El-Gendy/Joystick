package com.joystick.app.presentation.gamedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.model.Screenshot
import com.joystick.app.domain.model.Trailer
import com.joystick.app.presentation.gamedetail.components.*
import com.joystick.app.ui.components.ErrorStateView
import com.joystick.app.ui.components.JoystickScaffold
import com.joystick.app.ui.theme.JoystickTheme

@Composable
fun GameDetailScreen(
    gameId: Int,
    onBackClick: () -> Unit,
    viewModel: GameDetailViewModel = hiltViewModel<GameDetailViewModel, GameDetailViewModel.Factory>(
        creationCallback = { it.create(gameId) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTrailer by viewModel.selectedTrailer.collectAsStateWithLifecycle()

    GameDetailScreenContent(
        uiState = uiState,
        selectedTrailer = selectedTrailer,
        onBackClick = onBackClick,
        onTrailerSelected = viewModel::onTrailerSelected,
        onTrailerDismissed = viewModel::onTrailerDismissed,
        retry = viewModel::retry
    )
}

@Composable
fun GameDetailScreenContent(
    uiState: GameDetailUiState,
    selectedTrailer: Trailer?,
    onBackClick: () -> Unit,
    onTrailerSelected: (Trailer) -> Unit,
    onTrailerDismissed: () -> Unit,
    retry: () -> Unit
) {
    JoystickScaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is GameDetailUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is GameDetailUiState.Error -> {
                    ErrorStateView(
                        errorMessage = state.message,
                        onRetry = retry
                    )
                }
                is GameDetailUiState.Success -> {
                    val game = state.game
                    val scrollState = rememberScrollState()

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Scrollable content
                        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                            GameDetailHero(
                                imageUrl = game.imageUrl,
                                gameName = game.name
                            )

                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = game.name,
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(Modifier.height(16.dp))

                                GameDetailStatsRow(
                                    rating = game.rating,
                                    ratingTop = 5,
                                    metacritic = game.metacritic,
                                    playtime = game.playtime
                                )
                                Spacer(Modifier.height(24.dp))

                                GameDetailInfoChips(
                                    released = game.released,
                                    isTba = game.isTba,
                                    website = game.website
                                )
                                Spacer(Modifier.height(24.dp))

                                GameDetailAboutSection(description = game.description)
                                Spacer(Modifier.height(32.dp))

                                if (state.isLoadingExtras || state.trailers.isNotEmpty()) {
                                    TrailerSection(
                                        trailers = state.trailers,
                                        isLoading = state.isLoadingExtras,
                                        onTrailerClick = onTrailerSelected
                                    )
                                    Spacer(Modifier.height(32.dp))
                                }

                                if (state.isLoadingExtras || state.screenshots.isNotEmpty()) {
                                    ScreenshotSection(
                                        screenshots = state.screenshots,
                                        isLoading = state.isLoadingExtras
                                    )
                                }
                                Spacer(Modifier.height(48.dp))
                            }
                        }

                        // Fixed back button — always visible on top
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(top = 40.dp, start = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            }

            selectedTrailer?.let { trailer ->
                TrailerPlayerDialog(
                    trailer = trailer,
                    onDismiss = onTrailerDismissed
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameDetailScreenPreview() {
    JoystickTheme {
        GameDetailScreenContent(
            uiState = GameDetailUiState.Success(
                game = GameDetail(
                    id = 1,
                    name = "The Witcher 3: Wild Hunt",
                    imageUrl = "",
                    imageUrlAdditional = "",
                    description = "The Witcher 3: Wild Hunt is a 2015 action role-playing game developed and published by Polish developer CD Projekt Red and is based on The Witcher series of fantasy novels by Andrzej Sapkowski.",
                    released = "2015-05-19",
                    rating = 4.5,
                    metacritic = 93,
                    website = "https://www.thewitcher.com/en/witcher3",
                    playtime = 103,
                    isTba = false
                ),
                screenshots = listOf(
                    Screenshot(1, ""),
                    Screenshot(2, ""),
                    Screenshot(3, ""),
                ),
                trailers = listOf(
                    Trailer(1, "Launch Trailer", "", ""),
                    Trailer(2, "Gameplay Trailer", "", ""),
                ),
                isLoadingExtras = false
            ),
            selectedTrailer = null,
            onBackClick = {},
            onTrailerSelected = {},
            onTrailerDismissed = {},
            retry = {}
        )
    }
}
