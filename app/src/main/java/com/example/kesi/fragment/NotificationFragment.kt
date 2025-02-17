package com.example.kesi.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.adapter.AlarmListAdapter
import com.example.kesi.api.AlarmApi
import com.example.kesi.model.AlarmDataDto
import com.example.kesi.model.AlarmType
import com.example.kesi.setting.RetrofitSetting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class NotificationFragment : Fragment() {
    val alarmDataList = ArrayList<AlarmDataDto>()
    val retrofit = RetrofitSetting.getRetrofit()
    private val alarmApi: AlarmApi = retrofit.create(AlarmApi::class.java)
    val alarmAdapter = AlarmListAdapter(alarmDataList)
    private val TAG = "Notification"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_notification, container, false)

        val alarmRv: RecyclerView = root.findViewById(R.id.alarmRv)
        alarmRv.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = alarmAdapter
        }

        getAlarm()
        return root;
    }

    private fun getAlarm(){
        alarmApi.getAlarmAll().enqueue(object: Callback<List<AlarmDataDto>>{
            override fun onResponse(call: Call<List<AlarmDataDto>>, response: Response<List<AlarmDataDto>>) {
                response.body()?.let {
                    alarmDataList.addAll(it.sortedByDescending { data -> data.createTime })
                    alarmAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<AlarmDataDto>>, t: Throwable) {
                Toast.makeText(context,"통신 실패", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "exception : " + t.message)
            }
        })
    }
}