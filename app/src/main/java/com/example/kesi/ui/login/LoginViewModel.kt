package com.example.kesi.ui.login

import android.util.Log
import androidx.credentials.Credential
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kesi.application.LoginService
import com.example.kesi.data.model.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginService: LoginService
):ViewModel() {
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    fun login(email: String, password: String) = viewModelScope.launch {
        if(email.isEmpty()){
            _loginState.value = LoginState.Error("이메일을 입력해주세요")
            return@launch
        }
        if(password.isEmpty()){
            _loginState.value = LoginState.Error("비밀번호를 입력해주세요")
            return@launch
        }

        _loginState.value = LoginState.Loading

        val result = loginService.loginWithEmail(email, password)

        _loginState.value = if (result.isSuccess)
            LoginState.Success(result.getOrNull()!!)
        else {
            Log.d("Login fail", result.exceptionOrNull()?.message ?: "unknow login error")
            LoginState.Error("로그인 실패")
        }
    }


    fun onGoogleLogin(credential: Credential) = viewModelScope.launch {
        _loginState.value = LoginState.Loading

        val result = loginService.onGoogleLogin(credential)

        _loginState.value =
            if (result.isSuccess) LoginState.Success(result.getOrNull()!!)
            else {
                Log.d("Login fail", result.exceptionOrNull()?.message ?: "unknow login error")
                LoginState.Error("로그인 실패")
            }
    }
}