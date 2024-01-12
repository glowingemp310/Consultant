package com.example.consultant.consultee_activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.consultant.OnItemClick
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultee.AdapterConsultantCategories
import com.example.consultant.databinding.ActivityAllConsultantsBinding
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop
import com.google.firebase.firestore.FirebaseFirestore

class AllConsultantsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAllConsultantsBinding
    var allConsultants=ArrayList<ModelHomeConsulteeTop>()
    lateinit var adapter: AdapterConsultantCategories
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllConsultantsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTopBar()
        showALlConsultants()
        initAdapter()
        onClick()
    }

    private fun onClick() {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            onBackPressed()
        }
    }


    private fun showALlConsultants()
    {
        val db = FirebaseFirestore.getInstance()
        val consultantRef = db.collection("clinics")

        consultantRef
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    val id = document.id

                    val consultantImage = document.getString("image") ?: ""
                    val consultantName = document.getString("Consultant Name") ?: ""
                    val clinicName = document.getString("Clinic Name") ?: ""
                    val phoneNo = document.getString("Phone no") ?: ""
                    val about = document.getString("About") ?: ""
                    val address = document.getString("Address") ?: ""
                    val cnic = document.getString("Cnic") ?: ""
                    val occupation = document.getString("Occupation") ?: ""
                    val documentImage = document.getString("DocumentImage") ?: ""

                    val openTime = document.getString("OpenTime") ?: ""
                    val closeTime = document.getString("CloseTime") ?: ""

                    val lists = ModelHomeConsulteeTop(id,consultantImage,consultantName,clinicName, address, phoneNo, cnic, about, occupation,documentImage, closeTime, openTime)
                    allConsultants.add(lists)
                }

                adapter.setDate(allConsultants)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting consultants", exception)
            }

    }

    private fun initAdapter()
    {
        adapter = AdapterConsultantCategories(allConsultants, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                val consultant = allConsultants[position]

                val intent = Intent(this@AllConsultantsActivity, ConsultantDetailActivity::class.java)

                intent.putExtra("consultantObj", consultant)
                startActivity(intent)

            }
        })


        binding.rvALlConsultants.adapter = adapter
    }

    private fun initTopBar() {
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
        binding?.topbarHome?.tvTopBarContent?.setText("All Consultants")
    }
}