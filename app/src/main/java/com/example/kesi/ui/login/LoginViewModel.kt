package com.example.kesi.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kesi.application.LoginService
import com.example.kesi.data.model.LoginState
import com.google.firebase.auth.AuthCredential
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
        _loginState.value = LoginState.Loading

        val result = loginService.loginWithEmail(email, password)

        _loginState.value = if (result.isSuccess)
            LoginState.Success(result.getOrNull()!!)
        else LoginState.Error(result.exceptionOrNull()?.message)
    }


    fun login(credential: AuthCredential) = viewModelScope.launch {
        _loginState.value = LoginState.Loading

        val result = loginService.loginWithCredential(credential)

        _loginState.value = if (result.isSuccess)
            LoginState.Success(result.getOrNull()!!)
        else LoginState.Error(result.exceptionOrNull()?.message)
    }
}