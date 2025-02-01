package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.databinding.ItemAlarmListBinding
import com.example.kesi.holder.AlarmListViewHolder
import com.example.kesi.model.AlarmDataDto
import com.example.kesi.model.MessageDto

class AlarmListAdapter(val alarmList : ArrayList<AlarmDataDto>) : RecyclerView.Adapter<AlarmListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AlarmListViewHolder {
        val binding = ItemAlarmListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AlarmListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
        val binding = holder.binding
        //binding.icon.setImageResource(R.drawable.ic_notification)
        //binding.
    }

}