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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeRange(private val start: DateTimeTv,
                    private val end: DateTimeTv) {
    fun setStartDate(startDate: LocalDate) {
        start.setDate(startDate)

        if(startDate > end.getDate())
            end.setDate(startDate)
    }

    fun setEndDate(endDate: LocalDate) {
        end.setDate(endDate)

        if(endDate < start.getDate())
            start.setDate(endDate)
    }

    fun setStartTime(startTime: LocalTime) {
        start.setTime(startTime)

        if(start.getDateTime() > end.getDateTime())
            end.setTime(startTime)
    }

    fun setEndTime(endTime: LocalTime) {
        end.setTime(endTime)

        if(end.getDateTime() < start.getDateTime())
            start.setTime(endTime)
    }

    fun getStartDate() = start.getDate()
    fun getEndDate() = end.getDate()
    fun getStartTime() = start.getTime()
    fun getEndTime() = end.getTime()
}
class DateTimeTv(
    private var date: LocalDate,
    private var time: LocalTime,
    private val dateTv: TextView,
    private val timeTv: TextView
){
    val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREAN)
    val timeFormat = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN)

    init {
        setDate(date)
        setTime(time)
    }

    fun setDateTime(dateTime: LocalDateTime) {
        setDate(dateTime.toLocalDate())
        setTime(dateTime.toLocalTime())
    }

    fun setDate(date: LocalDate) {
        this.date = date
        dateTv.text = dateFormat.format(date)
    }

    fun setTime(time: LocalTime) {
        this.time = time
        timeTv.text = timeFormat.format(time)
    }

    fun getDate() = date
    fun getTime() = time

    fun getDateTime() = LocalDateTime.of(date, time)
}

class AddScheduleActivity : AppCompatActivity() {
//    lateinit var binding:ActivityAddScheduleBinding
    private lateinit var binding: ActivityAddScheduleTempBinding
    private var selectedDateTv: TextView? = null
    private var selectedTimeTv: TextView? = null

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

        val startDateTime = DateTimeTv(LocalDate.now(), LocalTime.now(), startDateTv, startTimeTv)
        val endDateTime = DateTimeTv(LocalDate.now(), LocalTime.now().plusMinutes(10), endDateTv, endTimeTv)

        val dateTimeRange = DateTimeRange(startDateTime, endDateTime)

        initDateChangeEvent(dateTimeRange)

        colorAdapter = CircleColorAdapter(listOf(
            ContextCompat.getColor(this, R.color.schedule_red).toColor(),
            ContextCompat.getColor(this, R.color.schedule_pink).toColor(),
            ContextCompat.getColor(this, R.color.schedule_orange).toColor(),
            ContextCompat.getColor(this, R.color.schedule_yellow).toColor(),
            ContextCompat.getColor(this, R.color.schedule_green).toColor(),
        ))

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
                startDate = startDateTime.getDate().toString(),
                startTime = startDateTime.getTime().withNano(0).toString(),
                endDate = endDateTime.getDate().toString(),
                endTime = endDateTime.getTime().withNano(0).toString(),
                securityLevel = SecurityLevel.LOW
            )

            val intent = Intent()
            intent.putExtra("schedule", addScheduleDto)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    private fun toggleVisibility(view: View) {
        if(view.visibility == View.VISIBLE)
            view.visibility = View.GONE
        else
            view.visibility = View.VISIBLE
    }

    private fun initDateChangeEvent(dateTimeRange: DateTimeRange) {
        val startDateTv = binding.startDateTv
        val startTimeTv = binding.startTimeTv

        val endDateTv = binding.endDateTv
        val endTimeTv = binding.endTimeTv

        val calendarView = binding.calendarView
        val calendarLayout = binding.calendarLayout

        val timePicker = binding.timePicker
        val timeLayout = binding.timeLayout

        startDateTv.setOnClickListener {
            timeLayout.visibility = View.GONE

            //end target으로 펼치고 있을 때는 toggle를 실행하지 않음
            if(!(calendarLayout.visibility == View.VISIBLE && selectedDateTv == endDateTv))
                toggleVisibility(calendarLayout)

            selectedDateTv = startDateTv
            //calendarView에 date는 UTC기준 milli second를 기준으로 설정되어 있어 변환이 필요
            //LocalDate > Instant > Millis 로 변환
            calendarView.date = dateTimeRange.getStartDate()
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        endDateTv.setOnClickListener {
            timeLayout.visibility = View.GONE
            if(calendarLayout.visibility == View.GONE || selectedDateTv == endDateTv)
                toggleVisibility(calendarLayout)

            selectedDateTv = endDateTv
            calendarView.date = dateTimeRange.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }


        startTimeTv.setOnClickListener {
            calendarLayout.visibility = View.GONE
            if(timeLayout.visibility == View.GONE || selectedTimeTv == startTimeTv)
                toggleVisibility(timeLayout)

            selectedTimeTv = startTimeTv
            timePicker.hour = dateTimeRange.getStartTime().hour
            timePicker.minute = dateTimeRange.getStartTime().minute
        }


        endTimeTv.setOnClickListener {
            calendarLayout.visibility = View.GONE
            if(timeLayout.visibility == View.GONE || selectedTimeTv == endTimeTv)
                toggleVisibility(timeLayout)

            selectedTimeTv = endTimeTv
            timePicker.hour = dateTimeRange.getEndTime().hour
            timePicker.minute = dateTimeRange.getEndTime().minute
        }

        calendarView.setOnDateChangeListener { calendarView, y, m, d ->
            if(selectedDateTv == startDateTv)
                dateTimeRange.setStartDate(LocalDate.of(y, m + 1, d))

            if(selectedDateTv == endDateTv)
                dateTimeRange.setEndDate(LocalDate.of(y, m + 1, d))
        }

        timePicker.setOnTimeChangedListener { timePicker, h, m ->
            if(selectedTimeTv == startTimeTv)
                dateTimeRange.setStartTime(LocalTime.of(h, m))

            if(selectedTimeTv == endTimeTv)
                dateTimeRange.setEndTime(LocalTime.of(h, m))
        }

    }
}