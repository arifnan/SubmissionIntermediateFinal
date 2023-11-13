package com.example.submissionintermediate.data.api

import com.example.submissionintermediate.data.response.Login
import com.example.submissionintermediate.data.response.Register
import com.example.submissionintermediate.data.response.StoryList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    fun doLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<Login>

    @POST("register")
    @FormUrlEncoded
    fun doRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Register>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): Response<StoryList>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<Any>

    @GET("stories")
    suspend fun getStoriesLoc(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Response<StoryList>
}