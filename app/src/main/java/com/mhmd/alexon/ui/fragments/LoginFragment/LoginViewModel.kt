package com.mhmd.alexon.ui.fragments.LoginFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meeting.letsmeeting.base.util.Resource
import com.mhmd.alexon.domain.models.LoginResponse
import com.mhmd.alexon.domain.models.UserModel
import com.mhmd.alexon.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    private var _loginStateFlow = MutableStateFlow<Resource<LoginResponse>>(Resource.Idle())
    var loginStateFlow = _loginStateFlow.asStateFlow()


    fun login(userModel: UserModel) = viewModelScope.launch(Dispatchers.IO) {
        _loginStateFlow.emit(Resource.Loading())
        try {
            val response = loginRepository.login(userModel)
            if (response.isSuccessful) {
                //Log.d("testApp", response.body().toString())
                response.body()?.let {
                    _loginStateFlow.emit(Resource.Success(it))
                } ?: _loginStateFlow.emit(Resource.EmptyData())
            } else {
                Log.d("testApp" , response.code().toString())
                Log.d("testApp" , response.message().toString())
                _loginStateFlow.emit(Resource.Error(message =  response.message().toString()))
            }

        } catch (e: Exception) {
            Log.d("testApp", e.message.toString())
            _loginStateFlow.emit(Resource.Error(message =  e.message.toString()))
        }
    }

}