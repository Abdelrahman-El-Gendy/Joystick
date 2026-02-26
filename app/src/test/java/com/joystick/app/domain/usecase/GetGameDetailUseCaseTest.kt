package com.joystick.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetGameDetailUseCaseTest {

    private val repository = mockk<GameRepository>()
    private val getGameDetailUseCase = GetGameDetailUseCase(repository)

    @Test
    fun `invoke returns Success with GameDetail when repository succeeds`() = runTest {
        val gameDetail = GameDetail(
            id = 1,
            name = "Test Game",
            imageUrl = null,
            imageUrlAdditional = null,
            description = "Description",
            released = "2023-01-01",
            rating = 4.5,
            metacritic = 90,
            website = "url",
            playtime = 10,
            isTba = false
        )
        coEvery { repository.getGameDetail(1) } returns Result.success(gameDetail)

        val result = getGameDetailUseCase(1)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(gameDetail)
        coVerify(exactly = 1) { repository.getGameDetail(1) }
    }

    @Test
    fun `invoke returns Failure when repository throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { repository.getGameDetail(2) } returns Result.failure(exception)

        val result = getGameDetailUseCase(2)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        coVerify(exactly = 1) { repository.getGameDetail(2) }
    }
}
