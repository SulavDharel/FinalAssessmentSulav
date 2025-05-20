// File: app/src/test/java/com/example/FinalAssessment/LoginViewModelTest.kt

package com.example.FinalAssessment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.FinalAssessment.data.repository.AppRepository
import com.example.FinalAssessment.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class) // Enables Mockito for mocking
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Ensures LiveData runs synchronously

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: AppRepository // Mocked repository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success should update live data`() = runTest {
        // Given: a successful login returns a keypass
        val keypass = "testKeypass"
        `when`(repository.login("username", "password", "location"))
            .thenReturn(Result.success(keypass))

        // When: login is triggered
        viewModel.login("username", "password", "location")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: loginResult should be success with correct keypass
        val result = viewModel.loginResult.value
        assert(result != null)
        assert(result!!.isSuccess)
        assert(result.getOrNull() == keypass)

        // And: loading should be false after the call
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun `login failure should update live data with error`() = runTest {
        // Given: login attempt fails with an exception
        val exception = Exception("Login failed")
        `when`(repository.login("username", "password", "location"))
            .thenReturn(Result.failure(exception))

        // When: login is triggered
        viewModel.login("username", "password", "location")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: loginResult should contain the exception
        val result = viewModel.loginResult.value
        assert(result != null)
        assert(result!!.isFailure)
        assert(result.exceptionOrNull()?.message == "Login failed")

        // And: loading should be false after the call
        assert(viewModel.isLoading.value == false)
    }
}
