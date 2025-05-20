// app/src/main/java/com/example/FinalAssessment/MyApplication.kt
package com.example.FinalAssessment

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // Enable Hilt dependency injection for the application
class MyApplication : Application()