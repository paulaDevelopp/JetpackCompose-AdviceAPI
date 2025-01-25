package com.example.gemaroom2.api

import retrofit2.http.GET
import retrofit2.Call

interface ApiService {
    @GET("users")
    fun getUsers(): Call<List<User>>
}



