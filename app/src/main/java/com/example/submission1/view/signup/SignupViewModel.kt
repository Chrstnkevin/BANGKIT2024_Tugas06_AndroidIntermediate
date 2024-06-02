package com.example.submission1.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission1.data.UserRepository
import com.example.submission1.data.response.AddStoryGuestResponse
import com.example.submission1.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val userRepo: UserRepository) : ViewModel() {

    private val _signupResponse = MutableLiveData<RegisterResponse>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun signup(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val message = userRepo.signup(name, email, password)
                _signupResponse.value = message // Assuming SignupResponse contains relevant data
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, AddStoryGuestResponse::class.java)
                val errorMessage = errorBody.message ?: "Unknown Error!!"
                _isError.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
