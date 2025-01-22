package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.databinding.ItemAlarmListBinding
import com.example.kesi.holder.AlarmListViewHolder
import com.example.kesi.model.MessageDto

class AlarmListAdapter(val alarmList : ArrayList<MessageDto>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val binding = ItemAlarmListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AlarmListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AlarmListViewHolder).binding
        //binding.icon.setImageResource(R.drawable.ic_notification)
        //binding.
    }

}