package com.void.movieboxtv.api

import com.void.movieboxtv.model.HomeCategory
import com.void.movieboxtv.model.PlayInfoResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface MovieBoxApi {
    @GET("home")
    suspend fun getHome(): List<HomeCategory>

    @GET("stream/{id}")
    suspend fun getStreamInfo(@Path("id") id: String): PlayInfoResponse
}

object RetrofitClient {
    // Agar TV pe Termux/server chal raha hai to 127.0.0.1, warna LAN IP e.g. 192.168.x.x:8000
    private const val BASE_URL = "http://127.0.0.1:8000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val api: MovieBoxApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieBoxApi::class.java)
    }
}
