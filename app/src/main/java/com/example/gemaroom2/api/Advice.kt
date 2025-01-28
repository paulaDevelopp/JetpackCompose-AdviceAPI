package com.example.gemaroom2.api

data class AdviceResponse(
    val slip: Advice
)

data class Advice(
    val id: Int,
    val advice: String
)
