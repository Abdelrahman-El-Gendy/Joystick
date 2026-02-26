package com.joystick.app.data.repository

import com.joystick.app.data.local.dao.GameDao
import com.joystick.app.data.remote.api.RawgApiService
import com.joystick.app.data.remote.mapper.toDomain
import com.joystick.app.data.remote.mapper.toEntity
import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.model.GamesPage
import com.joystick.app.domain.model.Screenshot
import com.joystick.app.domain.model.Trailer
import com.joystick.app.domain.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [GameRepository].
 * Delegates to [RawgApiService] and maps DTOs → domain via extension functions.
 *
 * All API calls are wrapped with [runCatching] — this class never throws.
 * Network and DB I/O is dispatched to [Dispatchers.IO].
 */
@Singleton
class GameRepositoryImpl @Inject constructor(
    private val api: RawgApiService,
    private val gameDao: GameDao
) : GameRepository {

    override suspend fun getGames(genre: String, page: Int): Result<GamesPage> =
        withContext(Dispatchers.IO) {
            if (page == 1) {
                val cachedResult = tryLoadFromCache(genre)
                if (cachedResult != null) return@withContext Result.success(cachedResult)
            }

            runCatching {
                val response = api.getGames(genre = genre, page = page).toDomain()
                if (page == 1) {
                    gameDao.clearGamesByGenre(genre)
                }
                gameDao.insertGames(response.games.map { it.toEntity(genre, page) })
                response
            }.recoverCatching { exception ->
                // Offline fallback for page 1 only
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

    override suspend fun getGameDetail(id: Int): Result<GameDetail> =
        withContext(Dispatchers.IO) {
            runCatching { api.getGameDetail(id = id).toDomain() }
        }

    override suspend fun getGameTrailers(id: Int): Result<List<Trailer>> =
        withContext(Dispatchers.IO) {
            runCatching { api.getGameTrailers(id).toDomain() }
        }

    override suspend fun getGameScreenshots(id: Int): Result<List<Screenshot>> =
        withContext(Dispatchers.IO) {
            runCatching { api.getGameScreenshots(id).toDomain() }
        }

    /**
     * Tries to load cached games for the given genre.
     * Returns null if cache is expired or empty.
     */
    private suspend fun tryLoadFromCache(genre: String): GamesPage? {
        val cachedAt = gameDao.getCacheTimestamp(genre) ?: return null
        val isCacheValid = (System.currentTimeMillis() - cachedAt) < CACHE_EXPIRY_MS

        if (!isCacheValid) return null

        val cached = gameDao.getGamesByGenre(genre).map { it.toDomain() }
        if (cached.isEmpty()) return null

        return GamesPage(
            games = cached,
            hasNextPage = true,
            totalCount = cached.size
        )
    }

    companion object {
        private const val CACHE_EXPIRY_MS = 10 * 60 * 1000L // 10 minutes
    }
}
