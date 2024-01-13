package com.example.consultant.consultant_fragments

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consultant.ContactUs
import com.example.consultant.PrivacyPolicy
import com.example.consultant.R
import com.example.consultant.TermsAndCondition
import com.example.consultant.adapter_classes_consultant.AdapterConsultantAppointments
import com.example.consultant.consultant_activities.AllAppointmentTypesActivity
import com.example.consultant.consultant_activities.RegisterClinicActivity
import com.example.consultant.databinding.FragmentConsultantHomeBinding
import com.example.consultant.model_classes_consultant.ModelConsultantAppointments
import com.example.consultant.user_auth.LoginActivity
import com.example.consultant.utils.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ConsultantHomeFragment : Fragment() {
    var binding:FragmentConsultantHomeBinding?=null
    private lateinit var adopter: AdapterConsultantAppointments
    val arrlist=ArrayList<ModelConsultantAppointments>()
    lateinit var DrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentConsultantHomeBinding?>(inflater,R.layout.fragment_consultant_home, container, false)
        return binding?.getRoot()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adopter = AdapterConsultantAppointments(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTopBar()
        onClick()
        showBarberAppointments()
        initDrawer()
    }

    private fun onClick() {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            binding?.homeDrawer?.openDrawer(GravityCompat.START)
        }

        binding?.tvRegisterClinic?.setOnClickListener {
            val intent= Intent(requireContext(), RegisterClinicActivity::class.java)
            startActivity(intent)
        }

        binding?.tvSeeAll?.setOnClickListener {
            val intent = Intent(requireContext(), AllAppointmentTypesActivity::class.java)
            startActivity(intent)
        }

        binding?.tvViewAppointments?.setOnClickListener {
            val intent = Intent(requireContext(), AllAppointmentTypesActivity::class.java)
            startActivity(intent)
        }

        binding?.tvContactus?.setOnClickListener {
            val intent = Intent(requireContext(), ContactUs::class.java)
            startActivity(intent)
        }
        binding?.tvPrivacyPolicy?.setOnClickListener {
            val intent = Intent(requireContext(), PrivacyPolicy::class.java)
            startActivity(intent)
        }
        binding?.tvTermCondition?.setOnClickListener {
            val intent = Intent(requireContext(), TermsAndCondition::class.java)
            startActivity(intent)
        }

        binding?.tvLogout?.setOnClickListener {
            SharedPreference.shared.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }
    }

    private fun initDrawer() {
        DrawerToggle= ActionBarDrawerToggle(requireContext() as Activity?, binding?.homeDrawer,R.string.nav_open, R.string.nav_close)
        binding?.homeDrawer?.addDrawerListener(DrawerToggle)
        DrawerToggle.syncState()
    }

    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Home")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.menu))
    }



    private fun showBarberAppointments() {
        binding?.rvAppointsList?.layoutManager = LinearLayoutManager(requireContext())
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val consultantId = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("bookings")
                .whereEqualTo("Consultant Id", consultantId)
                .get()
                .addOnSuccessListener { result ->
                    val appointmentList = ArrayList<ModelConsultantAppointments>()
                    for (document in result) {
                        val customerId = document.getString("userId")
                        if (customerId != null) {
                            // fetch customer name from users collection using customerId
                            db.collection("users").document(customerId)
                                .get()
                                .addOnSuccessListener { userDocument ->
                                    val customerName = userDocument.getString("full name") ?: "Unknown"
                                    val bookingDay = document.getString("Booking day") ?: ""
                                    val status = document.getString("Status") ?: ""
                                    val timing = document.getString("BookingTime") ?: ""

                                    val appointmentId = document.id
                                    val appointment = ModelConsultantAppointments(appointmentId,customerName, bookingDay,timing,status )
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

    }


}