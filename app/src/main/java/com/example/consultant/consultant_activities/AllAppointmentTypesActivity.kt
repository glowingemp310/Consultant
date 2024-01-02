package com.example.consultant.consultant_activities

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.adapters.RadioGroupBindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultant.AdapterAppointmentTypes
import com.example.consultant.adapter_classes_consultant.AdapterConsultantAppointments
import com.example.consultant.databinding.ActivityAllAppointmentTypesBinding
import com.example.consultant.databinding.ActivityAllConsultantsBinding
import com.example.consultant.model_classes_consultant.ModelAppointmentType
import com.example.consultant.model_classes_consultant.ModelConsultantAppointments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllAppointmentTypesActivity : AppCompatActivity() {
    lateinit var binding:ActivityAllAppointmentTypesBinding
    val adopter= AdapterConsultantAppointments(this)
    var items = arrayListOf<ModelAppointmentType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllAppointmentTypesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTopBar()
        topAppointmentTypes()
        showConsultantAppointments()
        onClick()
    }

    private fun onClick() {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showConsultantAppointments() {
        binding?.rvAppointsList?.layoutManager = LinearLayoutManager(this)
        val appointmentList = ArrayList<ModelConsultantAppointments>()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val consultantId = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("bookings")
                .whereEqualTo("Consultant Id", consultantId) // filter by shopId to show only appointments for the current barber's shop
                .get()
                .addOnSuccessListener { result ->

                    for (document in result) {
                        val customerId = document.getString("userId")
                        if (customerId != null) {
                            // fetch customer name from users collection using customerId
                            db.collection("users").document(customerId)
                                .get()
                                .addOnSuccessListener { userDocument ->
                                    val customerName = userDocument.getString("full name") ?: "Unknown"
                                    val bookingDay = document.getString("Booking day") ?: ""
                                    val timeSlot = document.getString("BookingTime") ?: ""
                                    val status = document.getString("Status") ?: ""
                                    val appointmentId = document.id
                                    val appointment = ModelConsultantAppointments(appointmentId,customerName, bookingDay, timeSlot, status)
                                    appointmentList.add(appointment)
                                    adopter.set_Data(appointmentList)
                                    binding?.rvAppointsList?.adapter = adopter

                                }
                                .addOnFailureListener { exception ->
                                    Log.d(ContentValues.TAG, "Error getting user document: ", exception)
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting appointments: ", exception)
                }
        }
        binding?.rvTopList?.adapter = AdapterAppointmentTypes(items, this) { prePosition: Int, selectedPost: Int, item: ModelAppointmentType ->
            if (item.title.equals("All")) {
                adopter.set_Data(appointmentList)
                binding?.rvAppointsList?.adapter = adopter
            } else {
                adopter.set_Data(appointmentList.filter { it.Status.equals(item.title,ignoreCase = true) } as ArrayList<ModelConsultantAppointments>)
                binding?.rvAppointsList?.adapter = adopter
            }
        }

    }

    private fun topAppointmentTypes() {

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
        binding?.topbarHome?.ivImageRight?.setVisibility(View.GONE)
        binding?.topbarHome?.tvTopBarContent?.setText("Appointments")
    }
}