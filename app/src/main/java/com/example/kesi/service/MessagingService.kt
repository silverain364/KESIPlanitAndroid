package com.example.kesi.service

import android.annotation.SuppressLint
import com.example.kesi.api.FCMApi
import com.example.kesi.setting.RetrofitSetting
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagingService: FirebaseMessagingService() {
    val retrofit = RetrofitSetting.getRetrofit()
    val fcmApi = retrofit.create(FCMApi::class.java)

    override fun onNewToken(token: String) { //새로운  토큰이 생성될 때 해당 콜백 함수가 자동으로 호출됨.
        super.onNewToken(token)
        fcmApi.addFCMToken(token).enqueue(object: Callback<String> { //새로운 토큰을 받게된 경우 SpringBoot 서버로 보냄
            override fun onResponse(p0: Call<String>, p1: Response<String>) {
            }

            override fun onFailure(p0: Call<String>, p1: Throwable) {
            }
        })
    }

    override fun onMessageReceived(message: RemoteMessage) { //메시지를 받았을 때 처리
        super.onMessageReceived(message)
    }
}