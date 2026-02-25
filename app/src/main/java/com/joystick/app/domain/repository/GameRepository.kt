package com.joystick.app.domain.repository

import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.model.GamesPage

/**
 * Repository contract for game data access.
 * Defined in the domain layer to enforce dependency inversion.
 *
 * All methods return [Result] — failures are wrapped, never thrown.
 */
interface GameRepository {

    /**
     * Fetches a paginated list of games filtered by genre.
     * @param genre Genre slug (e.g. "action", "rpg").
     * @param page  1-based page number.
     * @return [Result] wrapping a [GamesPage] on success, or a failure on error.
     */
    suspend fun getGames(genre: String, page: Int): Result<GamesPage>

    /**
     * Fetches full details for a single game.
     * @param id The unique game identifier.
     * @return [Result] wrapping a [GameDetail] on success, or a failure on error.
     */
    suspend fun getGameDetail(id: Int): Result<GameDetail>
}
