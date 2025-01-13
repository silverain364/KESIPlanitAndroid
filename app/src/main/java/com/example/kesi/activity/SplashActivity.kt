package com.example.kesi.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kesi.R
import com.example.kesi.setting.PreferenceUtil

class SplashActivity : AppCompatActivity() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    private val SPLASH_TIME_OUT: Long = 3000 // 3초
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        prefs = PreferenceUtil(this)

        askNotificationPermission();
    }

    fun movePage(){
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

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if(!it) { //거부당한 경우 activity를 새로 실행시켜 다시 물어봄.
            Toast.makeText(this, "알림 권한은 허용되어야 합니다.", Toast.LENGTH_SHORT).show();
            startActivity(Intent(this, SplashActivity::class.java));
            finish();
        }
    }

    private fun askNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //if(ContextCompat.checkSelfPermission(this, Manifest.permission.P) ==)
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                 == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();

                movePage()
            }
//            else if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)){
//                //처음에 권한을 거부하는 경우 다시 이 권한이 왜 필요한지 물어본다.
//                //Todo. Dialog 설계 필요
//            }
            else{
                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show();
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }else{
            movePage()
        }
    }
}