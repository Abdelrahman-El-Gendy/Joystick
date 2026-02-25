package com.joystick.app.domain.usecase

import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Use case for fetching full details of a single game.
 * Single-responsibility wrapper over [GameRepository.getGameDetail].
 */
class GetGameDetailUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: Int): Result<GameDetail> {
        return repository.getGameDetail(id = id)
    }
}
