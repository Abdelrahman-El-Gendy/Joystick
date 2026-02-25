package com.joystick.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ScreenshotDto(
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("is_deleted") val isDeleted: Boolean
)

data class ScreenshotsResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<ScreenshotDto>
)
