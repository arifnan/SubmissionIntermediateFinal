package com.example.submissionintermediate.data.response

import com.google.gson.annotations.SerializedName

data class User(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String
)
