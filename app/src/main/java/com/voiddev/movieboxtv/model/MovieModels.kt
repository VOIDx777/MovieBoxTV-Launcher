package com.voiddev.movieboxtv.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val code: Int = 0,
    val msg: String? = null,
    val data: HomeData? = null
)

data class HomeData(
    val list: List<HomeCategory>? = emptyList()
)

data class HomeCategory(
    val title: String? = "Category",
    val type: String? = null,
    val items: List<MovieItem> = emptyList()
)

data class MovieItem(
    @SerializedName("subjectId") val subjectId: String? = null,
    val id: String? = null,
    val title: String? = null,
    val name: String? = null,
    val cover: String? = null,
    val poster: String? = null,
    val score: String? = null,
    @SerializedName("releaseTime") val releaseTime: String? = null,
    val duration: String? = null,
    val desc: String? = null
) {
    val effectiveId: String
        get() = subjectId ?: id ?: ""

    val displayTitle: String
        get() = title ?: name ?: "Unknown"

    val displayPoster: String?
        get() = cover ?: poster
}

data class PlayInfoResponse(
    val code: Int = 0,
    val msg: String? = null,
    val url: String? = null,
    @SerializedName("stream_url") val streamUrl: String? = null,
    val headers: Map<String, String>? = null
) {
    fun getResolvedUrl(): String? = url ?: streamUrl
}
