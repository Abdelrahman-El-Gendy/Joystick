package com.joystick.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrailerDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("preview") val preview: String,
    @SerializedName("data") val data: TrailerDataDto
)

data class TrailerDataDto(
    @SerializedName("480") val low: String,
    @SerializedName("max") val max: String
)

data class TrailersResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<TrailerDto>
)
