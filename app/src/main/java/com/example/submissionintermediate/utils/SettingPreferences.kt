package com.example.submissionintermediate.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissionintermediate.auth.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constanta.preferenceName)

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>)  {


    suspend fun saveLoginSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] =user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = user.isLogin
        }
    }

    fun getLoginSession(): Flow<UserModel> {
        return dataStore.data.map { prefrences ->
            UserModel(
                prefrences[EMAIL_KEY] ?: "",
                prefrences[TOKEN_KEY] ?: "",
                prefrences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun clearLoginSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}