package com.example.submissionintermediate.utils

import android.content.Context
import com.example.submissionintermediate.auth.StoryRepository
import com.example.submissionintermediate.auth.UserRepository
import com.example.submissionintermediate.data.api.ApiConfig

object Injection {

    fun provideUserRepository(context: Context): UserRepository {
        val pref = SettingPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.apiInstance
        return UserRepository.getInstance(apiService,pref)
    }

    fun provideStoryRepository(): StoryRepository {
        val apiService = ApiConfig.apiInstance
        return StoryRepository.getInstance(apiService)
    }
}