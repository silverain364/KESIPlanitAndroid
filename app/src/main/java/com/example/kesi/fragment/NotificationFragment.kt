package com.example.kesi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.adapter.AlarmListAdapter
import com.example.kesi.model.AlarmDataDto
import com.example.kesi.model.AlarmType
import java.util.HashMap


class NotificationFragment : Fragment() {
    val alarmDataList = ArrayList<AlarmDataDto>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_notification, container, false)

        val alarmRv: RecyclerView = root.findViewById(R.id.alarmRv)

        for(i in 1..10)
            alarmDataList.add(AlarmDataDto(10L, "test", "test content", AlarmType.BASIC, "2022-12-10", HashMap()));
        val alarmAdapter = AlarmListAdapter(alarmDataList)

        alarmRv.layoutManager = LinearLayoutManager(activity)
        alarmRv.adapter = alarmAdapter;
        return root;
    }
}