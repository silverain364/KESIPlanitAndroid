package com.example.kesi.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.kesi.R
import com.example.kesi.data.model.LoginState
import com.example.kesi.databinding.ActivityLoginBinding
import com.example.kesi.setting.RetrofitSetting
import com.example.kesi.ui.main.MainActivity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private val retrofit = RetrofitSetting.getRetrofit()
    private val fcmApi = retrofit.create(com.example.kesi.data.remote.FCMApi::class.java)

    @Inject
    lateinit var credentialManager:CredentialManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etId.setText("ksh@naver.com")
        binding.etPassword.setText("ksh1234")

        //로그인 버튼 클릭 시 메인 화면으로 이동
        binding.btnLogin.setOnClickListener {
            if (viewModel.loginState.value == LoginState.Loading) return@setOnClickListener
            viewModel.login(binding.etId.text.toString(), binding.etPassword.text.toString())
        }

        //회원가입 버튼 클릭 시 회원가입 화면으로 이동
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        //게스트로 시작하기 버튼 클릭 시 메인화면으로 이동
        binding.tvGuestLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        credentialManager = CredentialManager.create(baseContext)


        binding.googleBtnLogint.setOnClickListener { //구글 버튼 클릭시
            if (viewModel.loginState.value == LoginState.Loading) return@setOnClickListener
//            awaitAll(viewModel.onClickGoogleLogin())
            launchCredentialManager()
        }



        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Idle -> {}
                is LoginState.Loading -> {}
                is LoginState.Success -> {
                    showToast("로그인 성공") //추후 nickName님 환영합니다.로 변경하면 좋을듯?

                    //Todo. 추후 삭제
                    state.user.getIdToken(true).addOnSuccessListener { getTokenResult ->
                        SplashActivity.prefs.setString(
                            "token",
                            getTokenResult.token!!
                        )
                    }
                    sendFCMToken(); //FCM token를 전송한다.

                    val intent = Intent(this, MainActivity::class.java) //토큰을 저장하고 이동하도록 수정
                    startActivity(intent)
                }

                is LoginState.Error -> {
                    showToast(state.message ?: "로그인 실패(알수 없는 에러)")
                }
            }
        }
    }

    private fun launchCredentialManager() {
        val googleIdOption = GetSignInWithGoogleOption.Builder( //로그인
            serverClientId = getString(R.string.default_web_client_id),
        ).build()


//        val googleIdOption2 =  GetGoogleIdOption.Builder() //자동 로그인
//            .setServerClientId(getString(R.string.default_web_client_id))
//            .build()

        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                // Launch Credential Manager UI
                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )

                viewModel.onGoogleLogin(result.credential)
            } catch (e: Exception) {
                Log.e(
                    "LoginActivity",
                    "Couldn't retrieve user's credentials: ${e.localizedMessage}"
                )
            }
        }
    }


    //캘린더 권한을 사용자에게 허락받았다는 가정하에 AuthCode를 받고 백엔드 서버로 넘긴다. 추후 구현
//    private fun receiveGoogleCalendarPermission(){
//        val googleSignClient = GoogleSignIn.getClient(
//            this,
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) //캘린더 권한을 요청
//                .build()
//        )
//
//        googleSignClient.silentSignIn().addOnCompleteListener(this){ //이미 캘린던 권한을 받았다면
//            if(it.isSuccessful){
//                val authCode = it.result.serverAuthCode //AuthCode를 받을 수 있다.
//                //AuthCode는 서버로 넘겨준다.
//                //추후 로직 설계 예정(테스트는 성공했지만 아직 App 들어가기 좀 애매해서 제외)
//
//                showToast("auth code : $authCode")
//            }else{
//                showToast(it.exception.toString())
//            }
//        }
//    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private fun sendFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { //FCM 토큰을 가져온다.
            if (!it.isSuccessful) {
                Log.w("firebase", "Fetching FCM registration token failed", it.exception)
                return@addOnCompleteListener
            }

            //토큰을 정상적으로 가져왔다면
            val token = it.result
            fcmApi.addFCMToken(token).enqueue(object : Callback<String> { //토큰을 SpringBoot 서버로 전송한다.
                override fun onResponse(p0: Call<String>, p1: Response<String>) {}
                override fun onFailure(p0: Call<String>, p1: Throwable) {
                    p1.message
                }
            })
        }
    }
}