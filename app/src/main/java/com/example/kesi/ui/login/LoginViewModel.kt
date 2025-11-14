package com.example.kesi.ui.login

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kesi.application.LoginService
import com.example.kesi.data.model.LoginState
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
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


    fun login(credential: AuthCredential) = viewModelScope.launch {
        _loginState.value = LoginState.Loading

        val result = loginService.loginWithCredential(credential)

        _loginState.value = if (result.isSuccess) {
            LoginState.Success(result.getOrNull()!!)
        }
        else LoginState.Error(result.exceptionOrNull()?.message)
    }

    fun onGoogleLoginResult(result: ActivityResult) {
        if(result.resultCode != Activity.RESULT_OK) {
            _loginState.value = LoginState.Error("Google Login Intent 결과값이 실패")
            return
        }

        val loginResult = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data!!)
        if(loginResult == null) {
            _loginState.value = LoginState.Error("Google Login Intent 결과값 가져오기 실패")
            return
        }

        if(!loginResult.isSuccess) {
            _loginState.value = LoginState.Error("로그인 실패")
            return
        }

        //Todo. Google Calendar 접근 권한 요청하기
        val account = loginResult.signInAccount
        val credential = GoogleAuthProvider.getCredential(account?.idToken!!, null)

        login(credential)
    }

    fun googleLogin() {

    }
}