package com.example.submissionintermediate.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionintermediate.data.api.ApiService
import com.example.submissionintermediate.data.response.Story
import com.example.submissionintermediate.data.response.StoryList
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    fun getStories(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }

    suspend fun getStoriesLocation(token: String) = apiService.getStoriesLoc(token)

    fun addNewStory(
        token: String,
        description: String,
        imageFile: File,
        lat: String?,
        lon: String?
    ): LiveData<Result<Story?>> {
        val responseLiveData: MutableLiveData<Result<Story?>> = MutableLiveData()
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val latPart = lat?.toRequestBody("text/plain".toMediaType())
        val lonPart = lon?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        responseLiveData.value = Result.Loading

        try {
            apiService.addNewStory("Bearer $token", requestBody, multipartBody, latPart, lonPart)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            responseLiveData.value = Result.Success(null)
                        } else {
                            responseLiveData.value = Result.Error(response.message())
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        responseLiveData.value = Result.Error(t.message.toString())
                    }
                })
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, Error::class.java)
            responseLiveData.value = Result.Error(errorResponse.message.toString())
        }
        return responseLiveData
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}
