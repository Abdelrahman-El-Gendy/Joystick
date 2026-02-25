package com.joystick.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for the paginated games list response.
 * Maps to: GET /games
 */
data class GamesResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<GameDto>
)

/**
 * DTO for a single game item within the games list.
 */
data class GameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("slug") val slug: String,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("rating_top") val ratingTop: Int,
    @SerializedName("metacritic") val metacritic: Int?,
    @SerializedName("genres") val genres: List<GenreDto>
)

/**
 * DTO for the full game detail response.
 * Maps to: GET /games/{id}
 */
data class GameDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("slug") val slug: String,
    @SerializedName("name") val name: String,
    @SerializedName("description_raw") val descriptionRaw: String?,
    @SerializedName("released") val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("background_image_additional") val backgroundImageAdditional: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("rating_top") val ratingTop: Int,
    @SerializedName("metacritic") val metacritic: Int?,
    @SerializedName("website") val website: String?,
    @SerializedName("playtime") val playtime: Int,
    @SerializedName("tba") val tba: Boolean
)

/**
 * DTO for a genre attached to a game.
 */
data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)
