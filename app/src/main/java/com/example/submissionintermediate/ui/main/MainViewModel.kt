package com.example.submissionintermediate.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissionintermediate.auth.StoryRepository
import com.example.submissionintermediate.auth.UserModel
import com.example.submissionintermediate.auth.UserRepository
import com.example.submissionintermediate.data.response.StoryList
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _storyResponse = MutableLiveData<StoryList>()
    val storyResponse: LiveData<StoryList> = _storyResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)

    fun login(email: String, password: String) = userRepository.login(email, password)

    fun getSession(): LiveData<UserModel> = userRepository.getLoginSession().asLiveData()

    fun setlogin(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveLoginSession(user)
        }
    }

    fun deleteLogin() {
        viewModelScope.launch {
            userRepository.clearLoginSession()
        }
    }

    fun getStories(token: String) = storyRepository.getStories(token)

    fun addNewStory(token: String, description: String, photo: File, lat: String?, lon: String?) =
        storyRepository.addNewStory(token, description, photo, lat, lon)

    fun getStoriesLocation(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = storyRepository.getStoriesLocation(token)
                Log.d(TAG, "onSuccess")
                _isLoading.postValue(false)
                _storyResponse.postValue(response.body())
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, StoryList::class.java)
                val errorMassage = errorBody.message
                _isLoading.postValue(false)
                Log.d(TAG, "onError: $errorMassage")
            }
        }
    }
    companion object {
        private const val TAG = "MainViewModel"
    }
}