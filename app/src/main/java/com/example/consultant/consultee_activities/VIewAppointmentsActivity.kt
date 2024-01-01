package com.example.consultant.consultee_activities

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultant.AdapterAppointmentTypes
import com.example.consultant.adapter_classes_consultee.AdapterViewAppointments
import com.example.consultant.databinding.ActivityViewAppointmentsBinding
import com.example.consultant.model_classes_consultant.ModelAppointmentType
import com.example.consultant.model_classes_consultee.ModelViewAppointments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VIewAppointmentsActivity : AppCompatActivity() {
    lateinit var binding:ActivityViewAppointmentsBinding
    var items = arrayListOf<ModelAppointmentType>()
    val adopter=AdapterViewAppointments(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityViewAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTopBar()
        topAppointmentTypes()
        setUpCustomerAppointListing()
        onClick()
    }

    private fun onClick() {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            onBackPressed()
        }
    }


    private fun setUpCustomerAppointListing() {
        val displayList = ArrayList<ModelViewAppointments>()
        binding?.rvCusAppointList?.layoutManager = LinearLayoutManager(this)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance().collection("bookings")
            db.whereEqualTo("userId", userId).get().addOnSuccessListener { result ->

                for (document in result) {
                    val consultantId = document.getString("Consultant Id")
                    if (consultantId != null) {
                        val shopRef = FirebaseFirestore.getInstance().collection("clinics").document(consultantId)
                        shopRef.get().addOnSuccessListener { shopDocument ->
                            val clinicName = shopDocument.getString("Clinic Name") ?: ""
                            val date = document.getString("Booking day") ?: ""
                            val time = document.getString("BookingTime") ?: ""
                            val status = document.getString("Status") ?: ""
                            val appointment = ModelViewAppointments(clinicName, date, time, status)
                            displayList.add(appointment)
                            adopter.set_Data(displayList)
                            binding?.rvCusAppointList?.adapter = adopter
                        }.addOnFailureListener { exception ->
                            Log.d(ContentValues.TAG, "Error getting shop document: ", exception)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting bookings: ", exception)
            }
        }

        // Filter adapter setup
        binding?.rvTopList?.adapter = AdapterAppointmentTypes(items, this) { prePosition: Int, selectedPost: Int, item: ModelAppointmentType ->
            if (item.title.equals("All")) {
                adopter.set_Data(displayList)
                binding?.rvCusAppointList?.adapter = adopter
            } else {
                adopter.set_Data(displayList.filter { it.statusBooking.equals(item.title, ignoreCase = true) } as ArrayList<ModelViewAppointments>)
                binding?.rvCusAppointList?.adapter = adopter
            }
        }
    }





    private fun topAppointmentTypes() {
        binding?.rvTopList?.layoutManager = LinearLayoutManager(
            this, RecyclerView.HORIZONTAL,
            false
        ).apply { binding?.rvTopList?.layoutManager = this }

        items=arrayListOf<ModelAppointmentType>(
            ModelAppointmentType(

                "All",
                android.R.color.holo_orange_light,
                android.R.color.black,
            ),

            ModelAppointmentType(

                "Pending",
                android.R.color.holo_orange_light,
                android.R.color.black,
            ),

            ModelAppointmentType(

                "Rejected",
                android.R.color.holo_orange_light,
                android.R.color.black,
            ),

            ModelAppointmentType(

                "Completed",
                android.R.color.holo_orange_light,
                android.R.color.black,
            ),
        )

    }

    private fun initTopBar() {
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
        binding?.topbarHome?.tvTopBarContent?.setText("Appointments")
    }
}