// File: app/src/main/java/com/example/FinalAssessment/ui/dashboard/EntityAdapter.kt

package com.example.FinalAssessment.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.FinalAssessment.data.models.Entity
import com.example.FinalAssessment.databinding.ItemEntityBinding

// Adapter for displaying a list of dynamic entities on the dashboard
// Uses DiffUtil via ListAdapter for efficient UI updates
class EntityAdapter(private val onItemClick: (Entity) -> Unit) :
    ListAdapter<Entity, EntityAdapter.EntityViewHolder>(EntityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val binding = ItemEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ViewHolder class for binding entity data
    inner class EntityViewHolder(private val binding: ItemEntityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(entity: Map<String, Any>) {
            // Determine primary and secondary fields for display
            val primaryField = findPrimaryField(entity)
            binding.primaryText.text = entity[primaryField]?.toString() ?: "Unknown"

            val secondaryField = findSecondaryField(entity, primaryField)
            if (secondaryField != null && entity.containsKey(secondaryField)) {
                binding.secondaryText.text = entity[secondaryField].toString()
                binding.secondaryText.visibility = ViewGroup.VISIBLE
            } else {
                binding.secondaryText.visibility = ViewGroup.GONE
            }

            // Configure status badge if applicable
            val statusField = findStatusField(entity)
            if (statusField != null && entity.containsKey(statusField)) {
                val statusValue = entity[statusField].toString()
                binding.statusBadge.text = statusValue
                binding.statusBadge.visibility = ViewGroup.VISIBLE

                val (bgColor, textColor) = getStatusColors(statusField, statusValue)
                (binding.statusBadge.background as? GradientDrawable)?.apply {
                    setColor(Color.parseColor(bgColor))
                }
                binding.statusBadge.setTextColor(Color.parseColor(textColor))
            } else {
                binding.statusBadge.visibility = ViewGroup.GONE
            }
        }

        // Helper to determine primary display field
        private fun findPrimaryField(entity: Map<String, Any>): String {
            val preferred = listOf("commonName", "species", "name", "title", "dishName")
            return preferred.find { entity.containsKey(it) }
                ?: entity.keys.firstOrNull {
                    !it.contains("scientific", true) &&
                            !it.contains("description", true) &&
                            !it.contains("level", true) &&
                            !it.contains("status", true)
                } ?: entity.keys.first()
        }

        // Helper to determine secondary display field
        private fun findSecondaryField(entity: Map<String, Any>, primaryField: String): String? {
            val preferred = listOf("scientificName", "author", "subtitle", "origin")
            return preferred.find { entity.containsKey(it) && it != primaryField }
                ?: entity.keys.firstOrNull {
                    it != primaryField &&
                            !it.contains("description", true) &&
                            !it.contains("level", true) &&
                            !it.contains("status", true)
                }
        }

        // Helper to identify a field to use as a status indicator
        private fun findStatusField(entity: Map<String, Any>): String? {
            val preferred = listOf("careLevel", "conservationStatus", "difficulty", "status", "priority")
            return preferred.find { entity.containsKey(it) }
                ?: entity.keys.firstOrNull {
                    it.contains("level", true) ||
                            it.contains("status", true) ||
                            it.contains("difficulty", true)
                }
        }

        // Maps status values to background/text colors
        private fun getStatusColors(statusField: String, statusValue: String): Pair<String, String> {
            return when (statusValue.lowercase()) {
                "easy", "low", "good", "stable", "least concern" ->
                    "#E8F5E9" to "#2E7D32" // Green

                "moderate", "medium", "fair", "near threatened" ->
                    "#FFF8E1" to "#F57F17" // Amber

                "hard", "difficult", "high", "poor", "endangered", "vulnerable", "critical" ->
                    "#FFEBEE" to "#C62828" // Red

                else -> "#E3F2FD" to "#1565C0" // Default: Light Blue
            }
        }
    }

    // DiffUtil callback to optimize RecyclerView updates
    class EntityDiffCallback : DiffUtil.ItemCallback<Entity>() {
        override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }
}
