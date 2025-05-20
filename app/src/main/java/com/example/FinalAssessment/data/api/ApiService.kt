// File: app/src/main/java/com/example/finalassessment/data/api/ApiService.kt

package com.example.FinalAssessment.data.api

import com.example.FinalAssessment.data.models.DashboardResponse
import com.example.FinalAssessment.data.models.LoginRequest
import com.example.FinalAssessment.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Authentication endpoint - POST request with location path parameter and login credentials
    @POST("{location}/auth")
    suspend fun login(
        @Path("location") location: String, // Dynamic location (footscray/sydney/ort)
        @Body loginRequest: LoginRequest   // Username and password in request body
    ): Response<LoginResponse>

    // Dashboard endpoint - GET request with keypass path parameter
    @GET("dashboard/{keypass}")
    suspend fun getDashboard(
        @Path("keypass") keypass: String   // Keypass received from successful login
    ): Response<DashboardResponse>
}
