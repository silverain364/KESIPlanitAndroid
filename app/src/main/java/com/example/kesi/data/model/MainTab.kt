package com.example.kesi.data.model

import com.example.kesi.R


sealed class MainTab(val text: String, val title: String, val color: Int, val icon: Int) {
    data object Home: MainTab("홈", "KESI", R.color.nav_bottom_home, R.drawable.ic_home_white)
    data object Community: MainTab("목록", "목록", R.color.nav_bottom_list, R.drawable.ic_list_white)
    data object Notification: MainTab("알림", "알림", R.color.nav_bottom_alarm, R.drawable.ic_notification_white)
    data object Setting: MainTab("계정", "내 계정", R.color.nav_bottom_account, R.drawable.ic_account_white)

    companion object {
        val all = listOf(Home, Community, Notification, Setting)
    }
}