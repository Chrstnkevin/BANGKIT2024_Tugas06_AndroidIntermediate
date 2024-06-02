package com.example.submission1.di

import android.content.Context
import com.example.submission1.data.UserRepository
import com.example.submission1.data.api.ApiConfig
import com.example.submission1.data.pref.UserPreference
import com.example.submission1.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}