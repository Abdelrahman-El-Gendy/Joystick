package com.joystick.app.domain.usecase

import com.joystick.app.domain.model.Screenshot
import com.joystick.app.domain.repository.GameRepository
import javax.inject.Inject

class GetGameScreenshotsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: Int): Result<List<Screenshot>> =
        repository.getGameScreenshots(id)
}
