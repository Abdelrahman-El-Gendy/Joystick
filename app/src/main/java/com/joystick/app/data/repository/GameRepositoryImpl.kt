package com.joystick.app.data.repository

import com.joystick.app.data.remote.api.RawgApiService
import com.joystick.app.data.remote.mapper.toDomain
import com.joystick.app.data.remote.mapper.toEntity
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
    private val api: RawgApiService,
    private val gameDao: com.joystick.app.data.local.dao.GameDao
) : GameRepository {

    override suspend fun getGames(genre: String, page: Int): Result<GamesPage> {
        val CACHE_EXPIRY_MS = 10 * 60 * 1000L // 10 minutes
        
        if (page == 1) {
            val cachedAt = gameDao.getCacheTimestamp(genre)
            val isCacheValid = cachedAt != null &&
                (System.currentTimeMillis() - cachedAt) < CACHE_EXPIRY_MS
            
            if (isCacheValid) {
                val cached = gameDao.getGamesByGenre(genre).map { it.toDomain() }
                if (cached.isNotEmpty()) {
                    return Result.success(
                        GamesPage(
                            games = cached,
                            hasNextPage = true, // Simplified for cache
                            totalCount = cached.size
                        )
                    )
                }
            }
        }

        return runCatching {
            val response = api.getGames(genre = genre, page = page).toDomain()
            if (page == 1) {
                gameDao.clearGamesByGenre(genre)
            }
            gameDao.insertGames(response.games.map { it.toEntity(genre, page) })
            response
        }.recoverCatching { exception ->
            // Offline fallback for page 1
            if (page == 1) {
                val stale = gameDao.getGamesByGenre(genre).map { it.toDomain() }
                if (stale.isNotEmpty()) {
                    return@recoverCatching GamesPage(
                        games = stale,
                        hasNextPage = false,
                        totalCount = stale.size
                    )
                }
            }
            throw exception
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
