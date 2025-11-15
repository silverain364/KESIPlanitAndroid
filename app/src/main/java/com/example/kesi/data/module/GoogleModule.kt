package com.example.kesi.data.module

import android.content.Context
import com.example.kesi.R
import com.google.android.gms.common.api.Scope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleModule {
//    @Provides
//    @Singleton
//    @Named("login")
//    fun provideLoginAndJoinGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient =
//        GoogleSignIn.getClient(
//            context,
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //로그인 옵션
//                .requestIdToken(context.getString(R.string.default_web_client_id)) //User Token
////                .requestServerAuthCode(context.getString(R.string.default_web_client_id)) //AuthCode를 받기 위한 설정
//                .requestEmail()
////                .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) //특정 권한(캘린더 권한) 추가 요청
//                .build()
//        )
//    @Provides
//    @Singleton
//    @Named("calendar")
//    fun provideCalendarGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient =
//        GoogleSignIn.getClient(
//            context,
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) //캘린더 권한을 요청
//                .build()
//        )


}