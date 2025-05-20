// File: app/src/main/java/com/example/FinalAssessment/ui/dashboard/DashboardActivity.kt

package com.example.FinalAssessment.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.FinalAssessment.databinding.ActivityDashboardBinding
import com.example.FinalAssessment.ui.details.DetailsActivity
import com.example.FinalAssessment.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

// Enables Hilt dependency injection for this activity
@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels() // Injected ViewModel
    private lateinit var adapter: EntityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()   // Set up list and adapter
        setupObservers()      // Observe ViewModel LiveData
        setupLogoutButton()   // Handle logout

        // Extract keypass from intent
        val keypass = intent.getStringExtra("keypass") ?: ""
        if (keypass.isBlank()) {
            Toast.makeText(this, "Invalid keypass", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        // Fetch dashboard data
        viewModel.fetchDashboard(keypass)
    }

    // Initializes the RecyclerView with a layout manager and adapter
    private fun setupRecyclerView() {
        adapter = EntityAdapter { entity ->
            // On entity click, navigate to details screen
            val intent = Intent(this, DetailsActivity::class.java).apply {
                @Suppress("UNCHECKED_CAST")
                putExtra("entity", HashMap(entity) as HashMap<String, Any>)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // Observes LiveData from ViewModel and updates UI accordingly
    private fun setupObservers() {
        viewModel.entities.observe(this) { result ->
            result.fold(
                onSuccess = { entities ->
                    adapter.submitList(entities)
                    val isEmpty = entities.isEmpty()
                    binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
                },
                onFailure = { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    // Handles logout button click
    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            navigateToLogin()
        }
    }

    // Navigates user back to login screen and clears back stack
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
