package com.example.gemaroom2.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("advice")
    fun getRandomAdvice(): Call<AdviceResponse>
}
