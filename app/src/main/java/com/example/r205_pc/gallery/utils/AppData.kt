package com.example.r205_pc.gallery.utils

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by r205-pc on 20.04.2018.
 */
class AppData{
    companion object {
        fun getOAuth(context:Context):String{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString("OAuth", "")
        }
        fun setOAuth(OAuth:String, context:Context){
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString("OAuth", OAuth)
            editor.apply()
        }
    }
}