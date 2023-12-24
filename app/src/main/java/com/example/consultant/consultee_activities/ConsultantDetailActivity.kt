package com.example.consultant.consultee_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.consultant.R
import com.example.consultant.databinding.ActivityConsultantDetailBinding
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop

class ConsultantDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityConsultantDetailBinding
    var consultantObj:ModelHomeConsulteeTop?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityConsultantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

       consultantObj = intent.getSerializableExtra("consultantObj") as? ModelHomeConsulteeTop
        if (consultantObj != null) {

            Glide.with(binding.ivConsultant).load(consultantObj!!.consultantImage).into(binding.ivConsultant)
            binding.tvConsultantName.text = consultantObj!!.consultantName
            binding.tvClinicName.text = consultantObj!!.clinicName
            binding.tvAddress.text = consultantObj!!.address
            binding.tvAbout.text = consultantObj!!.about
            binding.tvOccupation.text = consultantObj!!.occupation
            binding.tvPhoneNo.text = consultantObj!!.phoneNo
            val timings = "${consultantObj!!.openTime} - ${consultantObj!!.closeTime}"
            binding.tvTimings.text= timings
        }

        onCLick()
        initTopBar()

    }

    private fun onCLick() {
        binding.btnBooking.setOnClickListener{
            val intent= Intent(this, BookingAppointmentActivity::class.java)
            intent.putExtra("id", consultantObj!!.id)
            intent.putExtra("Consultant name", consultantObj!!.consultantName)

            startActivity(intent)

        }
        binding.topbarHome.ivImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Consultant Detail")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
    }
}