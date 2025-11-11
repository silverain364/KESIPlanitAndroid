package com.example.kesi.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.databinding.ItemAlarmListBinding

class AlarmListAdapter(private val alarmList : ArrayList<com.example.kesi.data.model.AlarmDataDto>) : RecyclerView.Adapter<AlarmListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AlarmListViewHolder {
        val binding = ItemAlarmListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AlarmListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
        holder.bind(alarmList[position])
    }

    fun addItem(alarmDataDto: com.example.kesi.data.model.AlarmDataDto) {
        alarmList.add(alarmDataDto)
    }
}