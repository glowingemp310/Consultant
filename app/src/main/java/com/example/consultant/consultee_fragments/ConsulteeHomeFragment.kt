package com.example.consultant.consultee_fragments

import android.app.Activity
import android.content.ContentValues
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
import com.example.consultant.OnItemClick
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultee.AdapterHomeConsulteeBottom
import com.example.consultant.adapter_classes_consultee.AdapterHomeConsulteeTop
import com.example.consultant.consultee_activities.AllConsultantsActivity
import com.example.consultant.consultee_activities.ConsultantCategoriesActivity
import com.example.consultant.consultee_activities.ConsultantDetailActivity
import com.example.consultant.consultee_activities.VIewAppointmentsActivity
import com.example.consultant.databinding.FragmentConsulteeHomeBinding
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeBottom
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop
import com.example.consultant.user_auth.LoginActivity
import com.example.consultant.utils.SharedPreference
import com.google.firebase.firestore.FirebaseFirestore

class ConsulteeHomeFragment : Fragment() {
    var binding: FragmentConsulteeHomeBinding?=null
    val showList = ArrayList<ModelHomeConsulteeTop>()
    val showCategories = ArrayList<ModelHomeConsulteeBottom>()
    lateinit var DrawerToggle: ActionBarDrawerToggle



    lateinit var adapter: AdapterHomeConsulteeTop
    lateinit var adapterCategories: AdapterHomeConsulteeBottom

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentConsulteeHomeBinding?>(inflater,R.layout.fragment_consultee_home, container, false)
        return binding?.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupTopConsultant()
        showCategories()
        initDrawer()
        onClick()
        initTopBar()



    }

    private fun onClick() {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            binding?.homeDrawer?.openDrawer(GravityCompat.START)
        }

        binding?.tvLogout?.setOnClickListener {
            SharedPreference.shared.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }

        binding?.tvMyAppintments?.setOnClickListener {
            val intent = Intent(requireContext(), VIewAppointmentsActivity::class.java)
            startActivity(intent)
        }

        binding?.tvSeeAll?.setOnClickListener {
            val intent = Intent(requireContext(), AllConsultantsActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupTopConsultant() {
        val db = FirebaseFirestore.getInstance()
        val consultantRef = db.collection("clinics")

        consultantRef.get().addOnSuccessListener { documents ->
            val consultantList = ArrayList<ModelHomeConsulteeTop>()

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


                val consultants = ModelHomeConsulteeTop(
                    id,
                    consultantImage,
                    consultantName,
                    clinicName,
                    address,
                    phoneNo,
                    cnic,
                    about,
                    occupation,
                    documentImage,
                    closeTime,
                    openTime
                )

                consultantList.add(consultants)
            }
            adapter.setDate(consultantList)


        }.addOnFailureListener { exception ->
            Log.e(ContentValues.TAG, "Error getting shop documents")

        }
    }


    private fun showCategories()
    {
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_2,"heart specialist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_3,"dentist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.endocrinology,"endocrinologist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.family_physicians,"family physicians")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_4,"eye specialist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.gastroenterologist,"gastroenterologist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.oncologists,"oncologists")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.allergy,"allergist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_2,"heart specialist ")))

        adapterCategories.setDate(showCategories)
        binding?.rvConsulteeBottom?.adapter=adapterCategories

    }





    private fun initAdapter() {
        adapterCategories= AdapterHomeConsulteeBottom(showCategories, object :OnItemClick{

            override fun onClick(position: Int, type: String?, data: Any?) {
                super.onClick(position, type, data)
                if (type == "Occupation") {
                    val occupation = data as String
                    val intent = Intent(context, ConsultantCategoriesActivity::class.java)
                    intent.putExtra("selectedCategory", occupation)
                    startActivity(intent)
                }
            }
        })


        adapter = AdapterHomeConsulteeTop(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                if (showList.isNotEmpty() && position < showList.size) {
                    val consultant = showList[position]

                    val intent = Intent(requireContext(), ConsultantDetailActivity::class.java)
                    intent.putExtra("consultantObj", consultant)
                    startActivity(intent)
                }
            }
        })


       // binding?.rvConsultantTop?.adapter = adapter


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






}