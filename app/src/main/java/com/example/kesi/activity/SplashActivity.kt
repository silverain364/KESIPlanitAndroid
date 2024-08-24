package com.example.kesi.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.kesi.R
import com.example.kesi.setting.PreferenceUtil

class SplashActivity : AppCompatActivity() { //가장 먼저 실행되는 클래스라고 가정

    companion object{
        lateinit var prefs : PreferenceUtil
    }


    private val SPLASH_TIME_OUT: Long = 3000 // 3초
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceUtil(this)

        setContentView(R.layout.activity_splash)


        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // 일정 시간이 지나면 MainActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // 이전 키를 눌렀을 때 스플래시 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용 안 함으로 finish 처리
            finish()

        }, 2000) // 시간 2초 이후 실행
    }
}