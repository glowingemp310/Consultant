package com.example.consultant.utils

import android.app.Application

open class BaseApplication : Application() {
    companion object {
        var instance: BaseApplication? =
            null
        var inBackground = false
//        var fontInterSemiBold: Typeface? = null
//        var fontInterRegular: Typeface? = null
//        var fontSFPRORegular: Typeface? = null
//        var fontSFPROSemibold: Typeface? = null
    }

    override fun onCreate() {
//        serverUrl = serverurl
        super.onCreate()
        instance = this

//        fontInterSemiBold = Typeface.createFromAsset(assets, "font/inter_semibold.ttf")
//        fontInterRegular = Typeface.createFromAsset(assets, "font/inter_regular.ttf")
//        fontSFPRORegular = Typeface.createFromAsset(assets, "font/sf_pro_text_regular.otf")
//        fontSFPROSemibold = Typeface.createFromAsset(assets, "font/sf_pro_text_semibold.otf")

    }
}