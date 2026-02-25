package com.joystick.app.domain.usecase

import com.joystick.app.domain.model.Trailer
import com.joystick.app.domain.repository.GameRepository
import javax.inject.Inject

class GetGameTrailersUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: Int): Result<List<Trailer>> =
        repository.getGameTrailers(id)
}
