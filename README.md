# NIT3213 Mobile Application Development Final Assignment - Android Application

## Objective
This android application is developed as a part of victoria University's NIT3213 Mobile Application development Final Assignment.

It demonstrats:
-API integration
-UI development with the help of adaptive layout
-Clean architecture
-unit testing

## API integration
This application uses victoria university's studentid API. The URL for the given API is "https://nit3213api.onrender.com".

## Endpoints Used:

POST/{location}/auth : post method is used for authenticating user data. 
here the location is where victoria uni is based. i.e sydney, footscray or ort(online).

GET /dashboard/{keypass}: used to retrive information from the API.

## Features:

# login screen:
-provide username and password field
-three different locations
-validate user crediantial from the api.

# Dashboard screen :
-fetch data with the help of keypass
-display entities in recylcerview
-provide further details of the entities.

# Detail screen:
-Provide additional information of the entities including the discription

## Architecture
This application use Model-view-viewModel(MVVC) architecture.

## Library/ Dependencies used:

Retrofit + Gson for networking
Hilt for dependency injection
kotlin coroutine for asnc tasks
recylcerview and cardview for ui components
viewModel and liveData for architecture
Junit and Mockito for testing

## Testing
Unit testing is performed for ViewModels and Repository
Mockito is used for Mocked API responses.

## Project Structure:
com.example.FinalAssessment
|---data/
|   |-api/
|   |-models/
|   |-api/repository/
|---di/
|---ui/
|   |-dashboard/
|   |-details/
|   |-login/
|---MyApplication.kt
|---res/
|---Gradle Scripts

## UI/UX
-smart field haldling
-Error handling and error message for login failure, or API error.

## Setup Instructions:

# Software:
-Android Studio
-Andriod SDK 21 or higher
-Gridle 7.0+

# Installation:
-first clone the project in your android studio.
-wait for gradle to download all the dependencies
-choose an emulator and run the project. 

# Application:
1. Lauch the application
2. Enter your name in the username section and your victoria university id in password section.
3. choose the campus location through the drop down menu below login.
4. click login

5. Application prompt to the dashboard screen where list of all entities are displayed.
6. Click on the entity to see the full information about it.

Acknowledgements:
-Victoria Univerity - NIT3213
-API: https://nit3213api.onrender.com/
