package com.example.submissionintermediate.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.submissionintermediate.data.api.ApiService
import com.example.submissionintermediate.data.response.Error
import com.example.submissionintermediate.data.response.Login
import com.example.submissionintermediate.data.response.Register
import com.example.submissionintermediate.utils.SettingPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class UserRepository(
    private val apiService: ApiService,
    private val SettingPreferences: SettingPreferences
) {
    fun register(name: String, email: String, password: String): MutableLiveData<Result<Register?>> {
        val responseLiveData: MutableLiveData<Result<Register?>> = MutableLiveData()
        responseLiveData.value = Result.Loading
        try {
            apiService.doRegister(name, email, password).enqueue(object : Callback<Register>{
                override fun onResponse(call: Call<Register>, response: Response<Register>) {
                    if(response.isSuccessful){
                        responseLiveData.value  = Result.Success(response.body())
                    }else{
                        responseLiveData.value = Result.Error(response.message())
                    }
                }

                override fun onFailure(call: Call<Register>, t: Throwable) {
                    responseLiveData.value = Result.Error(t.message.toString())
                }

            })
        } catch (e: HttpException){

            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, Error::class.java)
            responseLiveData.value = Result.Error(errorResponse.message)
        }
        return  responseLiveData
    }

    fun login(email: String, password: String): MutableLiveData<Result<Login?>> {
        val responseLiveData: MutableLiveData<Result<Login?>> = MutableLiveData()
        responseLiveData.value = Result.Loading
        try {
            apiService.doLogin(email, password).enqueue(object : Callback<Login>{
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    Log.wtf("RESPONSE", response.isSuccessful.toString())
                    if(response.isSuccessful){
                        responseLiveData.value  = Result.Success(response.body())
                    }else{
                        responseLiveData.value = Result.Error(response.message())
                    }
                }

                override fun onFailure(call: Call<Login>, t: Throwable) {
                    responseLiveData.value = Result.Error(t.message.toString())
                }

            })
        } catch (e: HttpException){

            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, Error::class.java)
            responseLiveData.value = Result.Error(errorResponse.message)
        }
        return responseLiveData
    }

    suspend fun saveLoginSession(user: UserModel){
        SettingPreferences.saveLoginSession(user)
    }

    fun getLoginSession(): Flow<UserModel> {
        return SettingPreferences.getLoginSession()
    }

    suspend fun clearLoginSession(){
        SettingPreferences.clearLoginSession()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(apiService: ApiService,userPreference: SettingPreferences) : UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService,userPreference)
            }.also { instance = it }
    }


}