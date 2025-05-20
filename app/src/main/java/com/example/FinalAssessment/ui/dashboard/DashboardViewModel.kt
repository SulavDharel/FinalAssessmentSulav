// File: app/src/main/java/com/example/assignmentlast/ui/dashboard/DashboardViewModel.kt

package com.example.FinalAssessment.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.FinalAssessment.data.models.Entity
import com.example.FinalAssessment.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel responsible for handling dashboard data and state
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AppRepository // Injected repository for data access
) : ViewModel() {

    // Backing property for exposing entity list with success/failure result
    private val _entities = MutableLiveData<Result<List<Entity>>>()
    val entities: LiveData<Result<List<Entity>>> = _entities

    // LiveData to expose loading state to the UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Fetches the dashboard entities from the repository using a keypass
    fun fetchDashboard(keypass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _entities.value = repository.getDashboard(keypass)
            _isLoading.value = false
        }
    }
}
