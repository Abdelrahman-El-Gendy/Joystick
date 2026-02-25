package com.joystick.app.data.remote.api

import com.joystick.app.data.remote.dto.GameDetailDto
import com.joystick.app.data.remote.dto.GamesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the RAWG Video Games API.
 * Base URL: https://api.rawg.io/api/
 *
 * The API key ("key" query param) is injected globally via [ApiKeyInterceptor].
 * Do NOT add a "key" parameter here.
 */
interface RawgApiService {

    /**
     * Fetches a paginated list of games, optionally filtered by genre slug.
     *
     * @param genre  Genre slug to filter by (e.g. "action", "rpg").
     * @param page   1-based page number.
     * @param pageSize Number of results per page.
     * @param ordering Sort order; defaults to highest-rated first.
     */
    @GET("games")
    suspend fun getGames(
        @Query("genres") genre: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20,
        @Query("ordering") ordering: String = "-rating"
    ): GamesResponseDto

    /**
     * Fetches full details for a single game.
     *
     * @param id The unique game identifier.
     */
    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int
    ): GameDetailDto
}
