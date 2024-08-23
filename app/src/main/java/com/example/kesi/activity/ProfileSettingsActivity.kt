package com.example.kesi.activity

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kesi.data.User
import com.example.kesi.databinding.ActivityProfileSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileSettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        auth = Firebase.auth

        //db 초기화
        database = Firebase.database.reference

        //인텐트로부터 email과 pw 받기
        val email = intent.getStringExtra("email")

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)

        //액션바의 기본 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //액션바의 기본 타이틀 숨김
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //회원가입 버튼 클릭 시 데이터베이스에 저장 후 로그인 화면으로 이동
        binding.btnSignUp.setOnClickListener {
            /*val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)*/
            if (email != null) {
                val nickname = binding.etNickname.text.toString().trim()
                val gender = when(binding.rgGender.checkedRadioButtonId) {
                    binding.rbMen.id -> "남자"
                    binding.rbWomen.id -> "여자"
                    else -> "선택되지 않음"
                }
                val birth = binding.etDOB.text.toString().trim()

                addUserToDatabase(auth.currentUser?.uid!!, email, nickname, gender, birth)

                val intent: Intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
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

    /*//회원가입 기능
    private fun signUp(email: String) {
        val nickname = binding.etNickname.text.toString().trim()
        val gender = when(binding.rgGender.checkedRadioButtonId) {
            binding.rbMen.id -> "남자"
            binding.rbWomen.id -> "여자"
            else -> "선택되지 않음"
        }
        val birth = binding.etDOB.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 성공 시 실행
                    Toast.makeText(this,"회원가입 성공",Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    addUserToDatabase(auth.currentUser?.uid!!, email, nickname, gender, birth)
                } else {
                    // 실패 시 실행
                    Toast.makeText(this,"회원가입 실패",Toast.LENGTH_SHORT).show()
                }
            }
    }*/

    private fun addUserToDatabase(uId: String, email: String, nickname: String, gender:String, birth:String) {
        database.child("user").child(uId).setValue(User(email = email, nickname = nickname, gender = gender, birth = birth))
    }
}