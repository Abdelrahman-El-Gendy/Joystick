package com.joystick.app.data.repository

import com.joystick.app.data.remote.api.RawgApiService
import com.joystick.app.data.remote.mapper.toDomain
import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.model.GamesPage
import com.joystick.app.domain.model.Screenshot
import com.joystick.app.domain.model.Trailer
import com.joystick.app.domain.repository.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [GameRepository].
 * Delegates to [RawgApiService] and maps DTOs → domain via extension functions.
 *
 * All API calls are wrapped with [runCatching] — this class never throws.
 */
@Singleton
class GameRepositoryImpl @Inject constructor(
    private val api: RawgApiService
) : GameRepository {

    override suspend fun getGames(genre: String, page: Int): Result<GamesPage> {
        return runCatching {
            api.getGames(genre = genre, page = page).toDomain()
        }
    }

    override suspend fun getGameDetail(id: Int): Result<GameDetail> {
        return runCatching {
            api.getGameDetail(id = id).toDomain()
        }
    }

    override suspend fun getGameTrailers(id: Int): Result<List<Trailer>> =
        runCatching { api.getGameTrailers(id).toDomain() }

    override suspend fun getGameScreenshots(id: Int): Result<List<Screenshot>> =
        runCatching { api.getGameScreenshots(id).toDomain() }
}
