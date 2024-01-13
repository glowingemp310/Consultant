package com.example.consultant

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.consultant.databinding.ActivityContactUsBinding

class ContactUs : AppCompatActivity() {
    var binding: ActivityContactUsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)

        initTopBar()
        onClickListener()
    }

    private fun onClickListener() {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            onBackPressed()
        }

        binding?.tvPhone?.setOnClickListener {
            val phoneNumber = binding?.tvPhone?.text?.toString()?.trim()

            if (phoneNumber?.isNotEmpty() == true) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phoneNumber")
                startActivity(intent)
            }
        }

        binding?.tvMail?.setOnClickListener {
            val email=binding?.tvMail?.text?.toString()?.trim()
            if (email?.isNotEmpty() == true) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:$email")
                startActivity(intent)
            }
        }

    }

    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Contact Us")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
    }
}