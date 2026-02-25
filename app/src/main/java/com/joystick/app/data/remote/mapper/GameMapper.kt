package com.joystick.app.data.remote.mapper

import com.joystick.app.data.remote.dto.GameDetailDto
import com.joystick.app.data.remote.dto.GameDto
import com.joystick.app.data.remote.dto.GamesResponseDto
import com.joystick.app.domain.model.Game
import com.joystick.app.domain.model.GameDetail
import com.joystick.app.domain.model.GamesPage
import com.joystick.app.data.remote.dto.TrailerDto
import com.joystick.app.data.remote.dto.TrailersResponseDto
import com.joystick.app.data.remote.dto.ScreenshotDto
import com.joystick.app.data.remote.dto.ScreenshotsResponseDto
import com.joystick.app.domain.model.Trailer
import com.joystick.app.domain.model.Screenshot

/**
 * Pure mapping functions: DTO → Domain.
 * These live in the data layer so the domain layer never sees DTO types.
 * All functions are pure — no side effects, no I/O.
 */

fun GameDto.toDomain(): Game = Game(
    id = id,
    name = name,
    imageUrl = backgroundImage,
    rating = rating,
    metacritic = metacritic,
    released = released
)

fun GameDetailDto.toDomain(): GameDetail = GameDetail(
    id = id,
    name = name,
    imageUrl = backgroundImage,
    imageUrlAdditional = backgroundImageAdditional,
    description = descriptionRaw,
    released = released,
    rating = rating,
    metacritic = metacritic,
    website = website,
    playtime = playtime,
    isTba = tba
)

fun GamesResponseDto.toDomain(): GamesPage = GamesPage(
    games = results.map { it.toDomain() },
    hasNextPage = next != null,
    totalCount = count
)

fun TrailerDto.toDomain() = Trailer(
    id = id,
    name = name,
    thumbnailUrl = preview,
    videoUrl = data.max
)

fun ScreenshotDto.toDomain() = Screenshot(
    id = id,
    imageUrl = image
)

fun TrailersResponseDto.toDomain() = results.map { it.toDomain() }

fun ScreenshotsResponseDto.toDomain() = results
    .filter { !it.isDeleted }
    .map { it.toDomain() }
