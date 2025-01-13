package com.example.kesi.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kesi.R
import com.example.kesi.api.FCMApi
import com.example.kesi.databinding.ActivityLoginBinding
import com.example.kesi.setting.RetrofitSetting
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleLoginLauncher : ActivityResultLauncher<Intent>

    private val retrofit = RetrofitSetting.getRetrofit()
    private val fcmApi = retrofit.create(FCMApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etId.setText("ksh@naver.com")
        binding.etPassword.setText("ksh1234")
        //로그인 버튼 클릭 시 메인 화면으로 이동
        binding.btnLogin.setOnClickListener {
            login(binding.etId.text.toString(), binding.etPassword.text.toString())
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


        //Google Login & Join
        val googleSignClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //로그인 옵션
                .requestIdToken(this.getString(R.string.default_web_client_id)) //User Token
                .requestServerAuthCode(getString(R.string.default_web_client_id)) //AuthCode를 받기 위한 설정
                .requestEmail()
                .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) //특정 권한(캘린더 권한) 추가 요청
                .build()
        )

        //구글 로그인 버튼 클릭시 실행할 페이지를 위한 Launcher
        googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                //결과 Intent(data 매개변수) 에서 구글로그인 결과 꺼내오기
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(it.data!!)!!

                //정상적으로 결과를 받았다면
                if(result.isSuccess){
                    val account = result.signInAccount

                    //구글로부터 로그인된 사용자의 정보(Credentail)을 얻어온다.
                    val credential = GoogleAuthProvider.getCredential(account?.idToken!!, null)

                    //그 정보를 사용하여 Firebase의 auth를 실행한다.
                    auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            receiveGoogleCalendarPermission() //허락 받은 내용으로 AuthCode를 얻고 백엔드 서버로 넘김

                            //페이지 이동
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            // 오류가 난 경우!
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }


                }else
                    Toast.makeText(this, "Login fail!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, " intent Fail!", Toast.LENGTH_SHORT).show()
            }
        }


        binding.googleBtnLogint.setOnClickListener { //구글 버튼 클릭시
            googleLoginLauncher.launch(googleSignClient.signInIntent)
        }
    }

    private fun login(email: String, pw: String){
        if(email.isEmpty()){
            showToast("이메일을 입력해주세요")
            return
        }
        if(pw.isEmpty()){
            showToast("비밀번호를 입력해주세요")
            return
        }

        auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener {
            if (it.isSuccessful) { //페이지 이동
                showToast("로그인 성공") //추후 nickName님 환영합니다.로 변경하면 좋을듯?
                auth.currentUser!!.getIdToken(true).addOnSuccessListener {
                    SplashActivity.prefs.setString("token", it.token!!)
                }

                sendFCMToken(); //FCM token를 전송한다.
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                showToast("이메일 또는 비밀번호가 틀렸습니다.")
            }
        }


    }



    //캘린더 권한을 사용자에게 허락받았다는 가정하에 AuthCode를 받고 백엔드 서버로 넘긴다.
    private fun receiveGoogleCalendarPermission(){
        val googleSignClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) //캘린더 권한을 요청
                .build()
        )

        googleSignClient.silentSignIn().addOnCompleteListener(this){ //이미 캘린던 권한을 받았다면
            if(it.isSuccessful){
                val authCode = it.result.serverAuthCode //AuthCode를 받을 수 있다.
                //AuthCode는 서버로 넘겨준다.
                //추후 로직 설계 예정(테스트는 성공했지만 아직 App 들어가기 좀 애매해서 제외)

                showToast("auth code : $authCode")
            }else{
                showToast(it.exception.toString())
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private fun sendFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { //FCM 토큰을 가져온다.
            if(!it.isSuccessful) {
                Log.w("firebase", "Fetching FCM registration token failed", it.exception)
                return@addOnCompleteListener
            }

            //토큰을 정상적으로 가져왔다면
            val token = it.result
            fcmApi.addFCMToken(token).enqueue(object: Callback<String> { //토큰을 SpringBoot 서버로 전송한다.
                override fun onResponse(p0: Call<String>, p1: Response<String>) {
                }
                override fun onFailure(p0: Call<String>, p1: Throwable) {
                }
            })
        }
    }

}