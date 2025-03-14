package com.example.kesi.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.R
import com.example.kesi.adapter.CircleColorAdapter
import com.example.kesi.data.AddScheduleDto
import com.example.kesi.databinding.ActivityAddScheduleTempBinding
import com.example.kesi.domain.SecurityLevel
import com.example.kesi.util.DateTimeRange
import com.example.kesi.util.DateTimeTv
import com.example.kesi.util.ScheduleColorList
import com.example.kesi.util.view.DateRangeSelectorView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*



class AddScheduleActivity : AppCompatActivity() {
//    lateinit var binding:ActivityAddScheduleBinding
    private lateinit var binding: ActivityAddScheduleTempBinding
    private var selectedDateTv: TextView? = null
    private var selectedTimeTv: TextView? = null
    private lateinit var dateRangeSelectorView: DateRangeSelectorView

    private lateinit var colorAdapter: CircleColorAdapter

    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
//      binding = ActivityAddScheduleBinding.inflate(layoutInflater)
        binding = ActivityAddScheduleTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleEt = binding.titleEt
        val addScheduleBtn = binding.scheduleAddBtn
        val textMemoEt = binding.textMemoEdit

        val startDateTv = binding.startDateTv
        val startTimeTv = binding.startTimeTv

        val endDateTv = binding.endDateTv
        val endTimeTv = binding.endTimeTv

        dateRangeSelectorView = DateRangeSelectorView(
            startDateTv = startDateTv,
            startTimeTv = startTimeTv,
            endDateTv = endDateTv,
            endTimeTv = endTimeTv,
            calendarLayout = binding.calendarLayout,
            timeLayout = binding.timeLayout
        )

        colorAdapter = CircleColorAdapter(ScheduleColorList.getColorList(this))

        val recyclerView = binding.colorRecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddScheduleActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorAdapter
        }


        addScheduleBtn.setOnClickListener {
            if(titleEt.text.toString().isBlank()) {
                Toast.makeText(this, "title를 입력해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val addScheduleDto = AddScheduleDto(
                colorValue = colorAdapter.getSelectedColor().toArgb(),
                title = titleEt.text.toString(),
                link = "",
                place = "",
                description = textMemoEt.text.toString(),
                startDate = dateRangeSelectorView.getStartDate().toString(),
                startTime = dateRangeSelectorView.getStartTime().withNano(0).toString(),
                endDate = dateRangeSelectorView.getEndDate().toString(),
                endTime = dateRangeSelectorView.getEndTime().withNano(0).toString(),
                securityLevel = SecurityLevel.LOW
            )

            val intent = Intent()
            intent.putExtra("schedule", addScheduleDto)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

}