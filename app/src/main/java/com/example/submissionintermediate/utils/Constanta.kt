package com.example.submissionintermediate.utils

object Constanta {

    enum class UserPreferences{
        UserUID, UserName, UserEmail, UserToken, UserLastLogin
    }

    const val preferenceName = "session"
    const val preferenceDefaultValue = "Not Set"
    const val preferenceDefaultDateValue = "2000/04/30 00:00:00"

    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
}