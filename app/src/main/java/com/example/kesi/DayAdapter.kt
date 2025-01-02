package com.example.kesi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DayAdapter(val tempMonth:Int, val dayList: MutableList<Date>) : RecyclerView.Adapter<DayAdapter.DayView>() {
    val ROW =6
    class DayView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_day,parent,false)

        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {

        //초기화
        var day_text: TextView = holder.layout.findViewById<TextView>(R.id.item_day_text)


        //날짜 표시
        day_text.text = dayList[position].date.toString()
        if(tempMonth != dayList[position].month) {
            day_text.alpha=0.4f
        }

        //토요일이면 파란색 || 일요일이면 빨간색으로 색상표시
        if((position + 1) % 7 == 0) {
            day_text.setTextColor(ContextCompat.getColor(holder.layout.context,R.color.sat))
        } else if (position == 0 || position % 7 == 0) {
            day_text.setTextColor(ContextCompat.getColor(holder.layout.context,R.color.sun))
        }
    }


    override fun getItemCount(): Int {
        return ROW*7
    }
}