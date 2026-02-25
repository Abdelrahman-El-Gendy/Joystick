package com.joystick.app.domain.usecase

import com.joystick.app.domain.model.GamesPage
import com.joystick.app.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Use case for fetching a paginated list of games filtered by genre.
 * Single-responsibility wrapper over [GameRepository.getGames].
 */
class GetGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(genre: String, page: Int): Result<GamesPage> {
        return repository.getGames(genre = genre, page = page)
    }
}
