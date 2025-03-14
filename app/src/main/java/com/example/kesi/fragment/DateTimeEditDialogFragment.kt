package com.example.kesi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.kesi.databinding.DialogDatetimeEditBinding
import com.example.kesi.util.DateTimeTv
import java.time.LocalDate
import java.time.LocalTime

class DateTimeEditDialogFragment: DialogFragment() {
    private lateinit var binding: DialogDatetimeEditBinding
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker

    private lateinit var targetDate: LocalDate
    private lateinit var targetTime: LocalTime

    private var saveBtnListener = OnClickListener { dismiss() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogDatetimeEditBinding.inflate(inflater, container, false)
        datePicker = binding.datePicker
        timePicker = binding.timePicker

        datePicker.updateDate(targetDate.year, targetDate.monthValue - 1, targetDate.dayOfMonth)
        timePicker.hour = targetTime.hour
        timePicker.minute = targetTime.minute

        binding.saveBtn.setOnClickListener(saveBtnListener)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true //화면 영역이나 뒤로가기시 닫힘
    }


    //사실 캡슐화 하려면 다른 객체가 한번 더 감싸기 해야됨
    fun showSetStartDateTimeEdit(fragmentSupport: FragmentManager, tag: String, startDateTimeTv: DateTimeTv, endDateTimeTv: DateTimeTv) {
        saveBtnListener = OnClickListener { //마지막에 설정한 리스너만 유효함
            startDateTimeTv.setDate(LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth))
            startDateTimeTv.setTime(LocalTime.of(timePicker.hour, timePicker.minute))
            if(startDateTimeTv.getDateTime() >= endDateTimeTv.getDateTime()) //시작 일정이 끝나는 일정 보다 뒤에 있는 경우
                endDateTimeTv.setDateTime(startDateTimeTv.getDateTime().plusMinutes(1)) //무조건 끝나는 일정이 뒤로 가게한다.
            dismiss()
        }

        targetDate = startDateTimeTv.getDate()
        targetTime = startDateTimeTv.getTime()

        show(fragmentSupport, tag)
    }

    fun showSetEndDateTimeEdit(fragmentSupport: FragmentManager, tag: String, startDateTimeTv: DateTimeTv, endDateTimeTv: DateTimeTv) {
        saveBtnListener = OnClickListener { //마지막에 설정한 리스너만 유효함
            endDateTimeTv.setDate(LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth))
            endDateTimeTv.setTime(LocalTime.of(timePicker.hour, timePicker.minute))
            if(startDateTimeTv.getDateTime() >= endDateTimeTv.getDateTime()) //시작 일정이 끝나는 일정 보다 뒤에 있는 경우
                startDateTimeTv.setDateTime(endDateTimeTv.getDateTime().minusMinutes(1)) //무조건 시작 일정이 앞으로 가게한다.
            dismiss()
        }

        targetDate = endDateTimeTv.getDate()
        targetTime = endDateTimeTv.getTime()

        show(fragmentSupport, tag)
    }
}