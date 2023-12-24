package com.example.consultant.consultee_activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.consultant.OnItemClick
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultee.AdapterConsultantCategories
import com.example.consultant.adapter_classes_consultee.AdapterHomeConsulteeTop
import com.example.consultant.databinding.ActivityConsultantCategoriesBinding
import com.example.consultant.model_classes_consultee.ModelConsultantCategories
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop
import com.google.firebase.firestore.FirebaseFirestore


class ConsultantCategoriesActivity : AppCompatActivity() {

    var consultantList=ArrayList<ModelHomeConsulteeTop>()
    lateinit var adapter:AdapterConsultantCategories
    var selectedCategory=""
    lateinit var binding:ActivityConsultantCategoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityConsultantCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
         selectedCategory = intent.getStringExtra("selectedCategory")?: ""

        if (selectedCategory.isNotEmpty()) {
            // The value is not null or empty, proceed with the logic
            initAdapter()
            showConsultants(selectedCategory)
        } else {
            // Handle the case where selectedCategory is null or empty
            Log.e("ConsultantCategories", "Selected category is null or empty.")
            // You might want to show an error message or take appropriate action.
        }

        onClick()
        initTopBar()
    }

    private fun onClick() {
        binding.topbarHome.ivImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showConsultants(category: String?)
    {
        val db = FirebaseFirestore.getInstance()
        val consultantRef = db.collection("clinics")

        consultantRef
            .whereEqualTo("Occupation", category)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    val id = document.id

                    val consultantImage = document.getString("image") ?: ""
                    val consultantName = document.getString("Consultant Name") ?: ""
                    val clinicName = document.getString("Clinic Name") ?: ""
                    val phoneNo = document.getString("Phone No") ?: ""
                    val about = document.getString("About") ?: ""
                    val address = document.getString("Address") ?: ""
                    val cnic = document.getString("Cnic") ?: ""
                    val occupation = document.getString("Occupation") ?: ""
                    val openTime = document.getString("OpenTime") ?: ""
                    val closeTime = document.getString("CloseTime") ?: ""

                   val lists = ModelHomeConsulteeTop(id,consultantImage,consultantName,clinicName, address, phoneNo, cnic, about, occupation, closeTime, openTime)
                    consultantList.add(lists)
                }

                adapter.setDate(consultantList)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting consultants", exception)
            }

    }

    private fun initAdapter()
    {
        adapter = AdapterConsultantCategories(consultantList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                val consultant = consultantList[position]

                val intent = Intent(this@ConsultantCategoriesActivity, ConsultantDetailActivity::class.java)

                intent.putExtra("consultantObj", consultant)
                startActivity(intent)

            }
        })


        binding.rvCategories.adapter = adapter
    }

    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Consultants")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
    }
}