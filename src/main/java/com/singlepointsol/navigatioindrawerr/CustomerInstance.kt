package com.singlepointsol.navigatioindrawerr

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CustomerInstance {

    companion object {
        private const val MAIN_URL = "https://abzcustomerwebapi-akshitha.azurewebsites.net/"

        private const val token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiYWRoaWxha3NobWkiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJkZXZlbG9wZXIiLCJleHAiOjE3MzU1NTYzODYsImlzcyI6Imh0dHBzOi8vd3d3LnRlYW0yLmNvbSIsImF1ZCI6Imh0dHBzOi8vd3d3LnRlYW0yLmNvbSJ9.xOMHHVReZFrrQAMPfE1NJudBVNQpNXoNtducNTmwYJs"
        fun getInstance(): Retrofit {
            // Configure OkHttpClient with an interceptor for the Authorization header
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token") // Add token to the header
                        .build()
                    chain.proceed(request)
                }
                .build()

            // Build Retrofit instance with OkHttpClient and Gson converter
            return Retrofit.Builder()
                .baseUrl(MAIN_URL)
                .client(client) // Attach the OkHttpClient with the interceptor
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}