package com.example.submission1.data

import com.example.submission1.data.api.ApiService
import com.example.submission1.data.pref.UserModel
import com.example.submission1.data.pref.UserPreference
import com.example.submission1.data.response.GetStoryResponse
import com.example.submission1.data.response.LoginResponse
import com.example.submission1.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun signup(name: String, email: String, password: String): RegisterResponse {
        return apiService.signup(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getStory(token: String): GetStoryResponse {
        return apiService.getStory(token)
    }

    suspend fun logout() {
        userPreference.logout() // Pastikan ini menghapus semua data session
    }

    suspend fun setAuth(user: UserModel) = userPreference.saveSession(user)

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }

    }
}