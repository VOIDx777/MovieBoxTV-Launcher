package com.voiddev.movieboxtv.model

import com.google.gson.annotations.SerializedName

data class HomeCategory(
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: String?,
    @SerializedName("items") val items: List<MovieItem> = emptyList()
)

data class MovieItem(
    @SerializedName("subjectId") val subjectId: String,
    @SerializedName("title") val title: String,
    @SerializedName("cover") val cover: String?,
    @SerializedName("poster") val poster: String?,
    @SerializedName("score") val score: String?,
    @SerializedName("releaseTime") val releaseTime: String?
)

data class PlayInfoResponse(
    @SerializedName("stream_url") val streamUrl: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("isHls") val isHls: Boolean = false,
    @SerializedName("headers") val headers: Map<String, String>? = null,
    @SerializedName("subtitles") val subtitles: List<SubtitleItem>? = null
) {
    fun getResolvedUrl(): String? = streamUrl ?: url
}

data class SubtitleItem(
    @SerializedName("id") val id: String,
    @SerializedName("lan") val lan: String,
    @SerializedName("lanName") val lanName: String,
    @SerializedName("url") val url: String
)
