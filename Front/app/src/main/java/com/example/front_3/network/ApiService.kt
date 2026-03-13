package com.example.front_3.network
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Ejercicio 1
    @GET("/")
    suspend fun checkApi(): Response<GeneralRes>

    // Ejercicio 2
    @POST("/register")
    suspend fun register(@Body request: RegisterReq): Response<AuthRes>

    // Ejercicio 3 y 4
    @POST("/login")
    suspend fun login(@Body request: LoginReq): Response<AuthRes>
}