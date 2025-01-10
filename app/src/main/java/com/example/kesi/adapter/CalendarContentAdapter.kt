package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.model.CalendarBoxDto
import androidx.fragment.app.FragmentManager

class CalendarContentAdapter(
    private val calendarBoxDtos: MutableList<CalendarBoxDto>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<CalendarContentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(calendarBoxDtos[position])
    }

    override fun getItemCount(): Int = calendarBoxDtos.size

    fun addItem(calendarBoxDto: CalendarBoxDto) {
        calendarBoxDtos.add(calendarBoxDto)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTv: TextView = itemView.findViewById(R.id.dayTv)

        fun bind(calendarBoxDto: CalendarBoxDto) {
            dayTv.text = calendarBoxDto.day.toString()
            if (calendarBoxDto.monthState != CalendarBoxDto.NOW_MONTH) {
                dayTv.setTextColor(itemView.context.resources.getColor(R.color.gray, null))
            }

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "${dayTv.text}을 클릭!", Toast.LENGTH_SHORT).show()
                // DialogFragment 추가 가능
            }
        }
    }
}