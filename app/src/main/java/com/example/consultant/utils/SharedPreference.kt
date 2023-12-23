package com.example.consultant.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit

class SharedPreference(private val mContext: Context?) {
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun clear() {
        preferences?.edit {
            clear()
        }
    }


    /* var islogin:Boolean
         get(){

             return preferences?.getBoolean("Login", false)?:false
         }
         set(isLogin){
             editor?.apply{
                 putBoolean("Login",isLogin)
                 apply()
             }
         }
*/
    // for consultee
    var isConsulteeLogin: Boolean
        get() {
            return preferences?.getBoolean("consultee_login", false) ?: false
        }
        set(isConsulteeLogin) {
            editor?.apply {
                putBoolean("consultee_login", isConsulteeLogin)
                apply()
            }
        }
    // for consultant
    var isConsultantLogin: Boolean
        get() {
            return preferences?.getBoolean("consultant_login", false) ?: false
        }
        set(isConsultantLogin) {
            editor?.apply {
                putBoolean("consultant_login", isConsultantLogin)
                apply()
            }
        }





    fun saveLogin(login: Boolean) {
        preferences?.edit { putBoolean("user_login", login) }
    }






    companion object {
        @JvmField
        var shared = SharedPreference(BaseApplication.instance)
    }


    init {
        preferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mContext?.getSharedPreferences(
                BaseApplication.instance?.packageName, Context.MODE_PRIVATE
            )
        } else {
            mContext?.getSharedPreferences(
                BaseApplication.instance?.packageName, Context.MODE_PRIVATE
            )
        }
        editor = preferences?.edit()
        editor?.apply()
    }

}