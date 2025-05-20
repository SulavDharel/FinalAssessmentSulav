// File: app/src/main/java/com/example/FinalAssessment/ui/details/DetailsActivity.kt

package com.example.FinalAssessment.ui.details

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.FinalAssessment.R
import com.example.FinalAssessment.databinding.ActivityDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

// Displays full details of a selected entity
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button click
        binding.backButton.setOnClickListener { finish() }

        // Load entity details from intent
        loadEntityDetails()
    }

    // Main function to load and display entity attributes
    private fun loadEntityDetails() {
        @Suppress("UNCHECKED_CAST")
        val entity = intent.getSerializableExtra("entity") as? HashMap<String, Any> ?: return

        // Set screen title
        val entityType = determineEntityType(entity)
        binding.toolbarTitle.text = "$entityType Details"

        binding.container.removeAllViews()

        val descriptionField = findDescriptionField(entity)
        val primaryFields = findPrimaryFields(entity)
        val statusFields = findStatusFields(entity)
        val remainingFields = entity.keys.filterNot {
            it in primaryFields || it in statusFields || it == descriptionField
        }.sorted()

        // Display primary fields (name, species, etc.)
        primaryFields.forEachIndexed { index, field ->
            entity[field]?.let {
                addLabelAndValue(
                    label = formatFieldName(field),
                    value = it.toString(),
                    isTitle = index == 0,
                    isItalic = field.contains("scientific", true),
                    topMargin = if (index > 0) dpToPx(16) else 0
                )
            }
        }

        // Display status fields as badges
        if (statusFields.isNotEmpty()) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dpToPx(24)
                }
            }

            statusFields.forEach { field ->
                entity[field]?.let { value ->
                    val statusLayout = createStatusLayout(field, value.toString())
                    statusLayout.layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    rowLayout.addView(statusLayout)
                }
            }

            if (rowLayout.childCount > 0) binding.container.addView(rowLayout)
        }

        // Display remaining fields
        remainingFields.forEach { field ->
            entity[field]?.let {
                addLabelAndValue(
                    label = formatFieldName(field),
                    value = it.toString(),
                    topMargin = dpToPx(16)
                )
            }
        }

        // Display description field last
        descriptionField?.takeIf { entity.containsKey(it) }?.let {
            addLabelAndValue(
                label = formatFieldName(it),
                value = entity[it].toString(),
                topMargin = dpToPx(24)
            )
        }
    }

    private fun determineEntityType(entity: Map<String, Any>): String = when {
        entity.containsKey("commonName") && entity.containsKey("scientificName") -> "Plant"
        entity.containsKey("species") && entity.containsKey("habitat") -> "Animal"
        entity.containsKey("dishName") || entity.containsKey("mealType") -> "Food"
        entity.containsKey("title") && entity.containsKey("author") -> "Book"
        else -> "Entity"
    }

    private fun findPrimaryFields(entity: Map<String, Any>): List<String> {
        val primary = listOf("dishName", "commonName", "species", "name", "title")
        val secondary = listOf("origin", "scientificName", "author", "subtitle")
        return listOfNotNull(
            primary.firstOrNull { entity.containsKey(it) },
            secondary.firstOrNull { entity.containsKey(it) }
        )
    }

    private fun findStatusFields(entity: Map<String, Any>): List<String> {
        val statusOptions = listOf(
            "mainIngredient", "mealType", "careLevel", "conservationStatus",
            "difficulty", "status", "lightRequirement", "habitat", "diet"
        )

        val matches = entity.keys.filter {
            statusOptions.contains(it) || listOf("level", "status", "requirement", "type", "ingredient")
                .any { keyword -> it.contains(keyword, ignoreCase = true) }
        }

        return matches.distinct().take(2)
    }

    private fun findDescriptionField(entity: Map<String, Any>): String? {
        return entity.keys.firstOrNull {
            it.contains("description", true) ||
                    it.contains("summary", true) ||
                    it.contains("about", true)
        }
    }

    private fun formatFieldName(field: String): String {
        return field.replaceFirstChar(Char::uppercaseChar)
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace("_", " ")
    }

    private fun createStatusLayout(field: String, value: String): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val label = TextView(this).apply {
            text = formatFieldName(field)
            textSize = 16f
            setTextColor(Color.parseColor("#4CAF50"))
        }
        layout.addView(label)

        val shouldBeBadge = listOf("level", "status", "difficulty")
            .any { field.contains(it, ignoreCase = true) }

        if (shouldBeBadge) {
            val (bgColor, textColor) = getStatusColors(field, value)
            val badge = TextView(this).apply {
                text = value
                textSize = 14f
                setTextColor(Color.parseColor(textColor))
                setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))

                val drawable = ContextCompat.getDrawable(this@DetailsActivity, R.drawable.badge_background)?.mutate()
                if (drawable is GradientDrawable) {
                    drawable.setColor(Color.parseColor(bgColor))
                    background = drawable
                }

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dpToPx(8) }
            }
            layout.addView(badge)
        } else {
            val valueView = TextView(this).apply {
                text = value
                textSize = 14f
                setTextColor(Color.parseColor("#212121"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dpToPx(8) }
            }
            layout.addView(valueView)
        }

        return layout
    }

    private fun getStatusColors(field: String, value: String): Pair<String, String> {
        return when (value.lowercase()) {
            "easy", "low", "good", "stable", "least concern" -> "#E8F5E9" to "#2E7D32"
            "moderate", "medium", "fair", "near threatened" -> "#FFF8E1" to "#F57F17"
            "hard", "difficult", "high", "poor", "endangered", "vulnerable", "critical" -> "#FFEBEE" to "#C62828"
            else -> "#E3F2FD" to "#1565C0"
        }
    }

    private fun addLabelAndValue(
        label: String,
        value: String,
        isTitle: Boolean = false,
        isItalic: Boolean = false,
        topMargin: Int = 0
    ) {
        val labelView = TextView(this).apply {
            text = label
            textSize = 16f
            setTextColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { this.topMargin = topMargin }
        }
        binding.container.addView(labelView)

        val valueView = TextView(this).apply {
            text = value
            textSize = if (isTitle) 24f else 16f
            setTextColor(Color.parseColor("#212121"))
            if (isTitle) setTypeface(null, Typeface.BOLD)
            if (isItalic) setTypeface(null, Typeface.ITALIC)
        }
        binding.container.addView(valueView)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
