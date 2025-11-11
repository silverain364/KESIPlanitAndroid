package com.example.kesi.ui.notification

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
import com.example.kesi.setting.RetrofitSetting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationFragment : Fragment() {
    val alarmDataList = ArrayList<com.example.kesi.data.model.AlarmDataDto>()
    val retrofit = RetrofitSetting.getRetrofit()
    private val alarmApi: com.example.kesi.data.remote.AlarmApi = retrofit.create(com.example.kesi.data.remote.AlarmApi::class.java)
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
        alarmApi.getAlarmAll().enqueue(object: Callback<List<com.example.kesi.data.model.AlarmDataDto>>{
            override fun onResponse(call: Call<List<com.example.kesi.data.model.AlarmDataDto>>, response: Response<List<com.example.kesi.data.model.AlarmDataDto>>) {
                response.body()?.let {
                    alarmDataList.addAll(it.sortedByDescending { data -> data.createTime })
                    alarmAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<com.example.kesi.data.model.AlarmDataDto>>, t: Throwable) {
                Toast.makeText(context,"통신 실패", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "exception : " + t.message)
            }
        })
    }
}