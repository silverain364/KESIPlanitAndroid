package com.example.kesi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MonthAdapter: RecyclerView.Adapter<MonthAdapter.Month>(){
    var calendar: Calendar = Calendar.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Month {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_month,parent,false)
        return Month(view)
    }


    override fun onBindViewHolder(holder: Month, position: Int) {


        //리사이클러뷰 초기화
        var list_layout: RecyclerView = holder.view.findViewById(R.id.month_recycler)


        //달 구하기


        calendar.time = Date() //현재 날짜 초기화
        calendar.set(Calendar.DAY_OF_MONTH,1) //스크롤시 현재 월의 1일로 이동
        calendar.add(Calendar.MONTH , position) //스크롤시 포지션 만큼 달이동

        //title 텍스트 초기화
//        var title_text: TextView =  holder.view.findViewById<TextView>(R.id.title)
//        var add_button: Button = holder.view.findViewById <Button>(R.id.add)
//
//        //현재 날짜 출력
//        title_text.setText("${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월")
        val tempMonth = calendar.get(Calendar.MONTH)

        //일 구하기


        //6주 7일로 날짜를 표시
        var dayList: MutableList<Date> = MutableList(6 * 7 ) { Date() }

        for(i in 0..5) { //주
            for (k in 0..6) { //요일
                //각 달의 요일만큼 캘린더에 보여진다
                //요일 표시
                calendar.add(Calendar.DAY_OF_MONTH, (1 - calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time //배열 인덱스 만큼 요일 데이터 저장
            }
            //주 표시
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        list_layout.layoutManager = GridLayoutManager(holder.view.context,7)
        list_layout.adapter = DayAdapter(tempMonth,dayList)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE / 2
    }

    class Month(val view: View) : RecyclerView.ViewHolder(view)
}