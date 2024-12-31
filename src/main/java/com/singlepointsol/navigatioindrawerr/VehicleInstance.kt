package com.singlepointsol.navigatioindrawerr

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VehicleInstance {
    companion object {
        private const val MAIN_URL = "https://abzvehiclewebapi-akshitha.azurewebsites.net/"

        private const val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiYWRoaSIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6ImRldmVsb3BlciIsImV4cCI6MTczNTY0NTA2NCwiaXNzIjoiaHR0cHM6Ly93d3cudGVhbTIuY29tIiwiYXVkIjoiaHR0cHM6Ly93d3cudGVhbTIuY29tIn0.0f4frvxMSJ8jkYlu7-Fzddhi9hXQ7vwa_scT1hJTUGE"

        fun getVehicleInstance(): Retrofit {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(MAIN_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}