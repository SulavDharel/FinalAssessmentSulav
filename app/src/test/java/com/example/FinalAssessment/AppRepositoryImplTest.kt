// File: app/src/test/java/com/example/FinalAssessment/AppRepositoryImplTest.kt

package com.example.FinalAssessment

import com.example.FinalAssessment.data.api.ApiService
import com.example.FinalAssessment.data.models.DashboardResponse
import com.example.FinalAssessment.data.models.LoginRequest
import com.example.FinalAssessment.data.models.LoginResponse
import com.example.FinalAssessment.data.repository.AppRepositoryImpl
import okhttp3.ResponseBody
import retrofit2.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class) // Enables Mockito for mocking
class AppRepositoryImplTest {

    @Mock
    private lateinit var apiService: ApiService // Mocked Retrofit service

    private lateinit var repository: AppRepositoryImpl

    @Before
    fun setup() {
        repository = AppRepositoryImpl(apiService)
    }

    @Test
    fun `login success should return keypass`() = runTest {
        // Given: API returns a successful login response
        val loginResponse = LoginResponse("test-keypass")
        `when`(apiService.login("location", LoginRequest("username", "password")))
            .thenReturn(Response.success(loginResponse))

        // When: login is invoked
        val result = repository.login("username", "password", "location")

        // Then: Result is successful with correct keypass
        assert(result.isSuccess)
        assert(result.getOrNull() == "test-keypass")
    }

    @Test
    fun `login failure should return error`() = runTest {
        // Given: API returns an error response for login
        val errorBody = ResponseBody.create(null, "Unauthorized")
        `when`(apiService.login("location", LoginRequest("username", "password")))
            .thenReturn(Response.error(401, errorBody))

        // When: login is invoked
        val result = repository.login("username", "password", "location")

        // Then: Result is a failure
        assert(result.isFailure)
    }

    @Test
    fun `getDashboard success should return entities`() = runTest {
        // Given: API returns dashboard response with 2 entities
        val entities = listOf(
            mapOf("name" to "Property 1", "scientificName" to "Property 2", "description" to "Description 1"),
            mapOf("name" to "Property 3", "scientificName" to "Property 4", "description" to "Description 2")
        )
        val dashboardResponse = DashboardResponse(entities = entities, entityTotal = 2)
        `when`(apiService.getDashboard("keypass"))
            .thenReturn(Response.success(dashboardResponse))

        // When: getDashboard is called
        val result = repository.getDashboard("keypass")

        // Then: Result is successful and list size matches
        assert(result.isSuccess)
        assert(result.getOrNull()?.size == 2)
    }

    @Test
    fun `getDashboard failure should return error`() = runTest {
        // Given: API returns server error
        val errorBody = ResponseBody.create(null, "Server error")
        `when`(apiService.getDashboard("keypass"))
            .thenReturn(Response.error(500, errorBody))

        // When: getDashboard is called
        val result = repository.getDashboard("keypass")

        // Then: Result is failure
        assert(result.isFailure)
    }
}
