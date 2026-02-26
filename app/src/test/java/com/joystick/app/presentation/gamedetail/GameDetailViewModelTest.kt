package com.joystick.app.presentation.gamedetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.joystick.app.MainDispatcherRule
import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.model.Screenshot
import com.joystick.app.domain.model.Trailer
import com.joystick.app.domain.usecase.GetGameDetailUseCase
import com.joystick.app.domain.usecase.GetGameScreenshotsUseCase
import com.joystick.app.domain.usecase.GetGameTrailersUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val savedStateHandle = mockk<SavedStateHandle> {
        every { get<Int>("gameId") } returns 123
    }
    private val getGameDetailUseCase = mockk<GetGameDetailUseCase>()
    private val getGameTrailersUseCase = mockk<GetGameTrailersUseCase>()
    private val getGameScreenshotsUseCase = mockk<GetGameScreenshotsUseCase>()

    private val mockGameDetail = GameDetail(
        id = 123,
        name = "Test Game",
        imageUrl = null,
        imageUrlAdditional = null,
        description = "Test Desc",
        released = null,
        rating = 4.0,
        metacritic = 80,
        website = null,
        playtime = 10,
        isTba = false
    )
    private val mockTrailers = listOf(Trailer(1, "Trailer 1", "preview_url", "video_url"))
    private val mockScreenshots = listOf(Screenshot(1, "image_url"))

    private fun createViewModel() = GameDetailViewModel(
        getGameDetailUseCase, getGameTrailersUseCase, getGameScreenshotsUseCase, savedStateHandle
    )

    @Test
    fun `initial state is Loading`() = runTest {
        coEvery { getGameDetailUseCase(123) } returns Result.success(mockGameDetail)
        coEvery { getGameTrailersUseCase(123) } returns Result.success(emptyList())
        coEvery { getGameScreenshotsUseCase(123) } returns Result.success(emptyList())

        val viewModel = createViewModel()
        assertThat(viewModel.uiState.value).isEqualTo(GameDetailUiState.Loading)
    }

    @Test
    fun `Success state after successful load`() = runTest {
        coEvery { getGameDetailUseCase(123) } returns Result.success(mockGameDetail)
        coEvery { getGameTrailersUseCase(123) } returns Result.success(emptyList())
        coEvery { getGameScreenshotsUseCase(123) } returns Result.success(emptyList())

        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(GameDetailUiState.Loading)
            val state = awaitItem() as GameDetailUiState.Success
            assertThat(state.game).isEqualTo(mockGameDetail)
            assertThat(state.isLoadingExtras).isTrue()
            
            // Advance for parallel loading completion
            advanceUntilIdle()
            val afterExtras = awaitItem() as GameDetailUiState.Success
            assertThat(afterExtras.isLoadingExtras).isFalse()
        }
    }

    @Test
    fun `Error state after failed load`() = runTest {
        coEvery { getGameDetailUseCase(123) } returns Result.failure(RuntimeException("Error loading"))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(GameDetailUiState.Loading)
            val state = awaitItem() as GameDetailUiState.Error
            assertThat(state.message).isEqualTo("Error loading")
        }
    }

    @Test
    fun `trailers and screenshots loaded in parallel after Success`() = runTest {
        coEvery { getGameDetailUseCase(123) } returns Result.success(mockGameDetail)
        coEvery { getGameTrailersUseCase(123) } returns Result.success(mockTrailers)
        coEvery { getGameScreenshotsUseCase(123) } returns Result.success(mockScreenshots)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as GameDetailUiState.Success
        assertThat(state.trailers).isEqualTo(mockTrailers)
        assertThat(state.screenshots).isEqualTo(mockScreenshots)
        assertThat(state.isLoadingExtras).isFalse()
    }

    @Test
    fun `retry calls loadGameDetail with same id`() = runTest {
        coEvery { getGameDetailUseCase(123) } returns Result.failure(RuntimeException("Error"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { getGameDetailUseCase(123) } returns Result.success(mockGameDetail)
        coEvery { getGameTrailersUseCase(123) } returns Result.success(emptyList())
        coEvery { getGameScreenshotsUseCase(123) } returns Result.success(emptyList())

        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value as GameDetailUiState.Success
        assertThat(state.game).isEqualTo(mockGameDetail)
    }

    @Test
    fun `onTrailerSelected updates selectedTrailer StateFlow`() = runTest {
        coEvery { getGameDetailUseCase(123) } returns Result.success(mockGameDetail)
        coEvery { getGameTrailersUseCase(123) } returns Result.success(mockTrailers)
        coEvery { getGameScreenshotsUseCase(123) } returns Result.success(mockScreenshots)
        val viewModel = createViewModel()

        val trailer = Trailer(1, "Name", "prev", "vid")
        viewModel.onTrailerSelected(trailer)

        assertThat(viewModel.selectedTrailer.value).isEqualTo(trailer)
    }

    @Test
    fun `onTrailerDismissed clears selectedTrailer`() = runTest {
        coEvery { getGameDetailUseCase(123) } returns Result.success(mockGameDetail)
        coEvery { getGameTrailersUseCase(123) } returns Result.success(mockTrailers)
        coEvery { getGameScreenshotsUseCase(123) } returns Result.success(mockScreenshots)
        val viewModel = createViewModel()

        val trailer = Trailer(1, "Name", "prev", "vid")
        viewModel.onTrailerSelected(trailer)
        viewModel.onTrailerDismissed()

        assertThat(viewModel.selectedTrailer.value).isNull()
    }
}
