package com.example.kesi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kesi.R
import com.example.kesi.databinding.ActivityAddScheduleBinding

class AddScheduleActivity : AppCompatActivity() {
    lateinit var binding:ActivityAddScheduleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rg.setOnCheckedChangeListener { radioGroup, i ->
            // 모든 ImageView를 비활성화
            listOf(binding.iv1, binding.iv2, binding.iv3, binding.iv4, binding.iv5).forEach { it.visibility = View.INVISIBLE }

            // 선택된 RadioButton에 따라 해당 ImageView를 활성화
            when (i) {
                R.id.rb1 -> binding.iv1.visibility = View.VISIBLE
                R.id.rb2 -> binding.iv2.visibility = View.VISIBLE
                R.id.rb3 -> binding.iv3.visibility = View.VISIBLE
                R.id.rb4 -> binding.iv4.visibility = View.VISIBLE
                R.id.rb5 -> binding.iv5.visibility = View.VISIBLE
            }
        }
    }
}