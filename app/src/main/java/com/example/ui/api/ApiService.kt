package com.example.api

import com.example.ui.MemberResponse
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

data class CreateAdminRequest(
    val whatsappNumber: String,
    val name: String,
    val email: String,
    val password: String
)

data class CreateEventRequest(
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val eventTasks: List<EventTask>,
    val eventMembers: List<EventMember>
)

data class EventTask(
    val description: String,
    val taskType: String,
    val createdAt: Long
)

data class EventMember(
    val memberWhatsapp: String
)

data class EventResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val eventTasks: List<EventTask>,
    val eventMembers: List<EventMember> // ⬅️ TAMBAH INI!
)

data class CreateEventResponse(
    val id: String
)

data class EventWithDetailsResponse(
    val event: EventResponse,
    val tasks: List<EventTask>,
    val members: List<EventMember>
)

data class Event(
    val id: Int,
    val name: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val createdBy: String
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

    @POST("admin")
    suspend fun createAdmin(@Body request: CreateAdminRequest): Response<Unit>

    @GET("members")
    suspend fun getMembers(): List<MemberResponse>

    @POST("/events")
    suspend fun createEvent(@Body body: CreateEventRequest): Response<CreateEventResponse>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: String): Response<EventWithDetailsResponse>

    @GET("/events")
    suspend fun getEvents(): List<EventWithDetailsResponse>
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
        .baseUrl("https://eventify-kerja-praktek-copy-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    return retrofit.create(ApiService::class.java)
}

// Fungsi untuk membuat ApiService tanpa token (untuk endpoint publik seperti /admin)
fun createPublicApiService(): ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://eventify-kerja-praktek-copy-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ApiService::class.java)
}

fun String?.truncate(maxLength: Int): String {
    return if (this == null) "-"
    else if (this.length > maxLength) this.take(maxLength) + "..."
    else this
}


