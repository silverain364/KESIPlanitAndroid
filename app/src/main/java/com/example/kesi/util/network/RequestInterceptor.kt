package com.example.kesi.util.network

import com.example.kesi.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RequestInterceptor @Inject constructor(
    private val tokenManager: TokenManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val auth = tokenManager.getToken() ?: String()

        if (auth.isNotEmpty()) requestBuilder.addHeader("Authorization", "Bearer $auth");

        return chain.proceed(requestBuilder.build())
    }
}