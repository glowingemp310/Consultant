package com.example.consultant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.consultant.bottom_navigation.BottomNavConsultant
import com.example.consultant.bottom_navigation.BottomNavConsultee
import com.example.consultant.user_auth.LoginActivity
import com.example.consultant.utils.SharedPreference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moveToLogin()

    }


    private fun moveToLogin()
    {
        if (SharedPreference.shared.isConsulteeLogin==true) {
            startActivity(Intent(this, BottomNavConsultee::class.java))
            finishAffinity()

        }

        else if (SharedPreference.shared.isConsultantLogin==true) {
            startActivity(Intent(this, BottomNavConsultant::class.java))
            finishAffinity()
        }

        else {
            Handler().postDelayed({
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        }
    }
}