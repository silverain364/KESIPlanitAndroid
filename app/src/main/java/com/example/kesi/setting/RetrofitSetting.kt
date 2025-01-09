package com.example.kesi.setting

import com.example.kesi.activity.MainActivity
import com.example.kesi.activity.SplashActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitSetting {
    companion object {
        //LOCAL GATEWAY ADDRESS
        //한 PC 내에서 모든 걸 테스트(API SERVER, MOBILE CLIENT)할 경우에 위 주소를 사용한다.
        val TEST_URL = "http://10.0.2.2:8080/"
        val IMAGE_URL = "http://49.50.164.110:8071/plan_it"
        private var interceptorClient: OkHttpClient? = null
        private var retrofit: Retrofit? = null


        //jwt 토큰이 만료된 경우 다시 요청을 해야한다.
        //jwt 토큰이 새로 생생된 경우 Interceptor를 변경할 필요가 있을 수도 있다
        //만들어 보니 함수형태라 필요 없을지도? 나중에 한번 확인해야 겠다
        fun reset(): Retrofit{
            interceptorClient = OkHttpClient.Builder()
                .addInterceptor(RequestInterceptor())
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(TEST_URL)
                .client(interceptorClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit!!;
        }

        fun getRetrofit(): Retrofit{
            return if(retrofit == null) reset()!! else retrofit!!
        }
    }

    class RequestInterceptor: Interceptor{
        override fun intercept(chain: Interceptor.Chain): Response {
            var requestBuilder = chain.request().newBuilder()
            var auth = SplashActivity.prefs.getString("token")!!

            if(auth.isNotEmpty()) requestBuilder.addHeader("Authorization", "Bearer $auth");

            return chain.proceed(requestBuilder.build())
        }

    }
}
