package com.joystick.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.joystick.app.domain.model.Game
import com.joystick.app.domain.model.GamesPage
import com.joystick.app.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetGamesUseCaseTest {

    private val repository = mockk<GameRepository>()
    private val getGamesUseCase = GetGamesUseCase(repository)

    @Test
    fun `invoke returns Success with GamesPage when repository succeeds`() = runTest {
        val gamesPage = GamesPage(
            games = listOf(
                Game(1, "Test Game", null, 4.5, 90, "2023-01-01")
            ),
            hasNextPage = true,
            totalCount = 100
        )
        coEvery { repository.getGames("action", 1) } returns Result.success(gamesPage)

        val result = getGamesUseCase("action", 1)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(gamesPage)
        coVerify(exactly = 1) { repository.getGames("action", 1) }
    }

    @Test
    fun `invoke returns Failure when repository throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { repository.getGames("action", 1) } returns Result.failure(exception)

        val result = getGamesUseCase("action", 1)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `invoke passes correct genre and page to repository`() = runTest {
        val gamesPage = GamesPage(emptyList(), false, 0)
        coEvery { repository.getGames("rpg", 2) } returns Result.success(gamesPage)

        getGamesUseCase("rpg", 2)

        coVerify(exactly = 1) { repository.getGames("rpg", 2) }
    }
}
