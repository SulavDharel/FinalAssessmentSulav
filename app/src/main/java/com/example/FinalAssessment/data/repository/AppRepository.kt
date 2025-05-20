// File: app/src/main/java/com/example/assignmentlast/data/repository/AppRepository.kt

package com.example.FinalAssessment.data.repository

import com.example.FinalAssessment.data.api.ApiService
import com.example.FinalAssessment.data.models.Entity
import com.example.FinalAssessment.data.models.LoginRequest
import javax.inject.Inject

// Repository interface defining abstract data operations for login and dashboard retrieval
interface AppRepository {
    suspend fun login(username: String, password: String, location: String): Result<String>
    suspend fun getDashboard(keypass: String): Result<List<Entity>>
}

// Concrete implementation of AppRepository using ApiService
class AppRepositoryImpl @Inject constructor(
    private val apiService: ApiService // Injected Retrofit API service
) : AppRepository {

    // Executes login by sending user credentials and location to the backend
    override suspend fun login(username: String, password: String, location: String): Result<String> {
        return try {
            val response = apiService.login(location, LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                // Return success with keypass from the response
                Result.success(response.body()!!.keypass)
            } else {
                // Return failure with HTTP error code and message
                Result.failure(Exception("Login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            // Return failure if any network or unexpected exception occurs
            Result.failure(e)
        }
    }

    // Fetches dashboard data using the keypass obtained after login
    override suspend fun getDashboard(keypass: String): Result<List<Entity>> {
        return try {
            val response = apiService.getDashboard(keypass)
            if (response.isSuccessful && response.body() != null) {
                // Return success with list of entities from the response
                Result.success(response.body()!!.entities)
            } else {
                // Return failure with HTTP error details
                Result.failure(Exception("Failed to get dashboard: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            // Return failure on exception (e.g., timeout, no internet)
            Result.failure(e)
        }
    }
}
