package com.example.kesi

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kesi.databinding.ActivityProfileSettingsBinding
import com.example.kesi.databinding.ActivitySignUpBinding

class ProfileSettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)

        //액션바의 기본 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //액션바의 기본 타이틀 숨김
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //회원가입 버튼 클릭 시 로그인 화면으로 이동
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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
}