package com.example.kesi.ui.community.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kesi.databinding.ActivityAddGroupScheduleBinding

class AddGroupScheduleActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddGroupScheduleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}