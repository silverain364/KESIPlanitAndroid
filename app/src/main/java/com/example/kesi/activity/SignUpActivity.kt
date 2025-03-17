package com.example.kesi.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kesi.databinding.ActivitySignUpBinding
import com.google.firebase.auth.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)

        //액션바의 기본 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //액션바의 기본 타이틀 숨김
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //Next 버튼 클릭 시 프로필 설정 화면으로 이동
        binding.signUpBtn.setOnClickListener {
            //val intent = Intent(this, ProfileSettingsActivity::class.java)
            //startActivity(intent)

            if (binding.etEmail.text.toString().isEmpty()) {
                showToast("이메일 채워야 합니다.")
            } else if (binding.etPassword.text.toString() != binding.etPasswordConfirm.text.toString()) {
                showToast("비밀번호와 비밀번호 확인 칸 내용이 다릅니다.")
            } else {
                join(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            }
        }
    }

    private fun join(email: String, pw: String) {
        //email과 password를 준다.
        auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener {  //통신 완료가 된 후 무슨일을 할지
            if (it.isSuccessful) {
                /*startActivity(Intent(this, ProfileSettingsActivity::class.java))*/
                auth.currentUser!!.getIdToken(true).addOnSuccessListener {
                    SplashActivity.prefs.setString("token", it.token!!)
                }
                val intent = Intent(this, ProfileSettingsActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            } else { //error 따른 Toast 메시지
                when (it.exception) {
                    is FirebaseAuthWeakPasswordException -> showToast("비밀번호가 너무 약합니다.")
                    is FirebaseAuthInvalidCredentialsException -> showToast("잘못된 이메일 형식 또는 비밀번호 형식입니다.")
                    is FirebaseAuthUserCollisionException -> showToast("이미 같은 이메일 주소로 가입된 사용자가 있습니다.")
                    is FirebaseAuthRecentLoginRequiredException -> showToast("보안상 이유로 사용자의 재인증이 필요합니다.")
                    else -> showToast("인증 관련 예외가 발생했습니다. ${it.exception.toString()}")
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //메뉴를 사용자가 선택했을 때의 이벤트 처리를 하는 함수(여기서는 뒤로가기 버튼 때문에 써줘야 함)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 뒤로가기 버튼 클릭 시 처리
                onBackPressedDispatcher.onBackPressed() // 뒤로가기 동작 수행
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}