package com.example.kesi.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.adapter.CircleColorAdapter
import com.example.kesi.data.EditScheduleDto
import com.example.kesi.databinding.ActivityEditScheduleBinding
import com.example.kesi.domain.SecurityLevel
import com.example.kesi.util.ActivityResultKeys
import com.example.kesi.util.DateTimeRange
import com.example.kesi.util.DateTimeTv
import com.example.kesi.util.ScheduleColorList
import com.example.kesi.util.view.DateRangeSelectorView
import com.google.android.material.button.MaterialButton.OnCheckedChangeListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EditScheduleActivity: AppCompatActivity() {
    private lateinit var binding: ActivityEditScheduleBinding
    private lateinit var dateRangeSelectorView: DateRangeSelectorView
    private lateinit var colorAdapter: CircleColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val edieScheduleDto: EditScheduleDto = (if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule", EditScheduleDto::class.java)
        }else {
            intent.getParcelableArrayExtra("schedule")
        } ?: throw RuntimeException("empty schedule!")) as EditScheduleDto

        val schedule = edieScheduleDto.toDomain()

        val titleEt = binding.titleEt
        val removeScheduleBtn = binding.removeScheduleBtn
        val editScheduleBtn = binding.editScheduleBtn
        val textMemoEt = binding.textMemoEdit
        val securityRatioGroup = binding.securityRadioGroup

        when(schedule.securityLevel) {
            SecurityLevel.HIGH -> binding.highRadioBtn.isChecked = true
            SecurityLevel.MEDIUM -> binding.mediumRadioBtn.isChecked = true
            SecurityLevel.LOW -> binding.lowRadioBtn.isChecked = true
        }

        titleEt.setText(schedule.title)
        textMemoEt.setText(schedule.description)
        //Todo. 나머지 요소들도 초기화 코드 필요

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
            timeLayout =  binding.timeLayout,
            startDateTime = LocalDateTime.of(schedule.start, schedule.startTime),
            endDateTime = LocalDateTime.of(schedule.end, schedule.endTime)
        )

        val scheduleColorList = ScheduleColorList.getColorList(this)

        colorAdapter = CircleColorAdapter(scheduleColorList, schedule.color)

        binding.colorRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditScheduleActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorAdapter
        }

        removeScheduleBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra(ActivityResultKeys.ACTION_TYPE, ActivityResultKeys.DELETE)
            intent.putExtra("scheduleId", schedule.id)
            setResult(RESULT_OK, intent)
            finish()
        }

        editScheduleBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra(ActivityResultKeys.ACTION_TYPE, ActivityResultKeys.EDIT)
            intent.putExtra("schedule", EditScheduleDto(
                id = schedule.id,
                colorValue = schedule.color.toArgb(),
                title = titleEt.text.toString(),
                link = "",
                place = "",
                description = textMemoEt.text.toString(),
                startDate = dateRangeSelectorView.getStartDate().toString(),
                startTime = dateRangeSelectorView.getStartTime().withNano(0).toString(),
                endDate = dateRangeSelectorView.getEndDate().toString(),
                endTime = dateRangeSelectorView.getEndTime().withNano(0).toString(),
                securityLevel = when(securityRatioGroup.checkedRadioButtonId) {
                    binding.highRadioBtn.id -> SecurityLevel.HIGH
                    binding.mediumRadioBtn.id -> SecurityLevel.MEDIUM
                    binding.lowRadioBtn.id -> SecurityLevel.LOW
                    else -> throw RuntimeException("no selected security level!")
                }
            ))
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}