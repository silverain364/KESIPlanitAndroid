package com.example.kesi.ui.common.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CalendarContentAdapter(
    private val calendarBoxDtos: MutableList<com.example.kesi.data.model.CalendarBoxDto>,
    private val fragmentManager: FragmentManager,
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
) : RecyclerView.Adapter<CalendarContentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(calendarBoxDtos[position])
    }

    override fun getItemCount(): Int = calendarBoxDtos.size

    fun addItem(calendarBoxDto: com.example.kesi.data.model.CalendarBoxDto) {
        calendarBoxDtos.add(calendarBoxDto)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTv: TextView = itemView.findViewById(R.id.dayTv)
        private val scheduleLegendRv = itemView.findViewById<RecyclerView>(R.id.scheduleLegendRv)

        fun bind(calendarBoxDto: com.example.kesi.data.model.CalendarBoxDto) {
            dayTv.text = calendarBoxDto.day.toString()
            if (calendarBoxDto.monthState != com.example.kesi.data.model.CalendarBoxDto.NOW_MONTH) {
                dayTv.setTextColor(itemView.context.resources.getColor(R.color.gray, null))
            }

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "${dayTv.text}을 클릭!", Toast.LENGTH_SHORT).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                // DialogFragment 추가 가능
            }


        }
    }
}


//class ScheduleLegendSpanSizeLookup: GridLayoutManager.SpanSizeLookup(){


//    override fun getSpanSize(position: Int): Int {
//
//    }

//}