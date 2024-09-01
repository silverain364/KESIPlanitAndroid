package com.example.kesi.setting

import android.content.Context
import com.google.android.gms.common.util.SharedPreferencesUtils

class PreferenceUtil(context: Context) {
    var prefs = context.getSharedPreferences("planIt", Context.MODE_PRIVATE);

    fun getString(key:String, defaultValue: String = ""):String? =
        prefs.getString(key, defaultValue)

    fun setString(key: String, value: String) =
        prefs.edit().putString(key, value).apply()

}