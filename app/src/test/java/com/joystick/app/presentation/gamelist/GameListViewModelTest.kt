package com.joystick.app.presentation.gamelist

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.joystick.app.MainDispatcherRule
import com.joystick.app.domain.model.Game
import com.joystick.app.domain.model.GamesPage
import com.joystick.app.domain.usecase.GetGamesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class GameListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val getGamesUseCase = mockk<GetGamesUseCase>()
    private val savedStateHandle = mockk<SavedStateHandle> {
        every { get<String>("genre") } returns "action"
    }

    private fun createViewModel() = GameListViewModel(getGamesUseCase, savedStateHandle)

    private val mockGamesPage = GamesPage(
        games = listOf(Game(1, "Game 1", null, 4.0, 80, null)),
        hasNextPage = true,
        totalCount = 10
    )

    @Test
    fun `initial state is InitialLoading`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(mockGamesPage)
        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value).isEqualTo(GameListUiState.InitialLoading)
    }

    @Test
    fun `Success state after successful loadGames`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(mockGamesPage)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(GameListUiState.InitialLoading)
            val successState = awaitItem() as GameListUiState.Success
            assertThat(successState.allGames).isEqualTo(mockGamesPage.games)
            assertThat(successState.filteredGames).isEqualTo(mockGamesPage.games)
            assertThat(successState.hasNextPage).isTrue()
        }
    }

    @Test
    fun `Error state after failed loadGames`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.failure(RuntimeException("Error"))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(GameListUiState.InitialLoading)
            val errorState = awaitItem() as GameListUiState.Error
            assertThat(errorState.message).isEqualTo("Error")
        }
    }

    @Test
    fun `Empty(NO_GENRE_RESULTS) when API returns empty list`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(GamesPage(emptyList(), false, 0))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(GameListUiState.InitialLoading)
            val emptyState = awaitItem() as GameListUiState.Empty
            assertThat(emptyState.reason).isEqualTo(EmptyReason.NO_GENRE_RESULTS)
        }
    }

    @Test
    fun `loadNextPage appends games to existing allGames`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(mockGamesPage)
        val nextPage = GamesPage(
            games = listOf(Game(2, "Game 2", null, 3.0, null, null)),
            hasNextPage = false,
            totalCount = 10
        )
        coEvery { getGamesUseCase("action", 2) } returns Result.success(nextPage)
        
        val viewModel = createViewModel()
        advanceUntilIdle() 
        
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as GameListUiState.Success
        assertThat(state.allGames).hasSize(2)
        assertThat(state.hasNextPage).isFalse()
    }

    @Test
    fun `onSearchQueryChanged filters filteredGames correctly`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(
            GamesPage(
                games = listOf(
                    Game(1, "Alpha", null, 4.0, null, null),
                    Game(2, "Beta", null, 4.0, null, null)
                ),
                hasNextPage = false,
                totalCount = 2
            )
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Alpha")

        val state = viewModel.uiState.value as GameListUiState.Success
        assertThat(state.filteredGames).hasSize(1)
        assertThat(state.filteredGames[0].name).isEqualTo("Alpha")
        assertThat(state.searchQuery).isEqualTo("Alpha")
    }

    @Test
    fun `onSearchQueryChanged empty query restores full allGames`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(
            GamesPage(
                games = listOf(
                    Game(1, "Alpha", null, 4.0, null, null),
                    Game(2, "Beta", null, 4.0, null, null)
                ),
                hasNextPage = false,
                totalCount = 2
            )
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Alpha")
        viewModel.onSearchQueryChanged("")

        val state = viewModel.uiState.value as GameListUiState.Success
        assertThat(state.filteredGames).hasSize(2)
        assertThat(state.searchQuery).isEmpty()
    }

    @Test
    fun `onSearchQueryChanged no match results in Empty(NO_SEARCH_RESULTS)`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(
            GamesPage(
                games = listOf(
                    Game(1, "Alpha", null, 4.0, null, null)
                ),
                hasNextPage = false,
                totalCount = 1
            )
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Zeta")

        val state = viewModel.uiState.value as GameListUiState.Empty
        assertThat(state.reason).isEqualTo(EmptyReason.NO_SEARCH_RESULTS)
    }

    @Test
    fun `pagination failure sets paginationError not full Error`() = runTest {
        coEvery { getGamesUseCase("action", 1) } returns Result.success(mockGamesPage)
        coEvery { getGamesUseCase("action", 2) } returns Result.failure(RuntimeException("Pagination Error"))
        
        val viewModel = createViewModel()
        advanceUntilIdle() 
        
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as GameListUiState.Success
        assertThat(state.paginationError).isEqualTo("Pagination Error")
        assertThat(state.allGames).isEqualTo(mockGamesPage.games) // Unchanged
    }
}
