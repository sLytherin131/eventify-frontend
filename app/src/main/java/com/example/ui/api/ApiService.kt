package com.example.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class CreateTaskRequest(
    val description: String,
    val taskType: String
)

data class TaskResponse(
    val id: Int,
    val adminWhatsapp: String,
    val description: String,
    val taskType: String,
    val createdAt: Long
)

interface ApiService {
    // Login endpoint
    @POST("/admin/login")
    suspend fun login(@Body request: LoginRequest): Response<ResponseBody>

    // Personal Task endpoints
    @GET("personal_tasks")
    suspend fun getTasks(): List<TaskResponse>

    @POST("personal_tasks")
    suspend fun addTask(@Body request: CreateTaskRequest): TaskResponse

    @DELETE("personal_tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int)
}

// Fungsi untuk membuat ApiService dengan token
fun createApiService(jwtToken: String): ApiService {
    val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $jwtToken")
                .build()
            chain.proceed(request)
        })
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8082")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    return retrofit.create(ApiService::class.java)
}
