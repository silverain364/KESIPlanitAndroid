package com.example.kesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kesi.databinding.ActivityAddFriendsBinding

class AddFriendsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddFriendsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
    // 뒤로가기 버튼을 눌렀을 때 호출되는 메서드
    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 액티비티 종료, 이전 액티비티로 돌아감
        return true
    }
}