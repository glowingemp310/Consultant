package com.example.consultant.consultee_fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consultant.OnItemClick
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultee.AdapterHomeConsulteeBottom
import com.example.consultant.adapter_classes_consultee.AdapterHomeConsulteeTop
import com.example.consultant.consultee_activities.ConsultantDetailActivity
import com.example.consultant.databinding.FragmentConsultantHomeBinding
import com.example.consultant.databinding.FragmentConsulteeHomeBinding
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeBottom
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class ConsulteeHomeFragment : Fragment() {
    var binding: FragmentConsulteeHomeBinding?=null
    val showList = ArrayList<ModelHomeConsulteeTop>()
    val showCategories = ArrayList<ModelHomeConsulteeBottom>()


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
                val phoneNo = document.getString("Phone No") ?: ""
                val about = document.getString("About") ?: ""
                val address = document.getString("Address") ?: ""
                val cnic = document.getString("Cnic") ?: ""
                val occupation = document.getString("Occupation") ?: ""
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
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_2,"Heart Specialist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_3,"Dentist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.endocrinology,"Endocrinologist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.family_physicians,"Family Physicians")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_4,"Eye Specialist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.gastroenterologist,"Gastroenterologist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.oncologists,"Oncologists")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.allergy,"Allergy Specialist")))
        showCategories.add((ModelHomeConsulteeBottom(R.drawable.img_2,"Heart Specialist")))

        adapterCategories.setDate(showCategories)
        binding?.rvConsulteeBottom?.adapter=adapterCategories

    }





    private fun initAdapter() {
        adapterCategories=AdapterHomeConsulteeBottom(showCategories)
        adapter = AdapterHomeConsulteeTop(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {

                val intent = Intent(context, ConsultantDetailActivity::class.java)
               // intent.putExtra("book_id", showList[position].id)
               // intent.putExtra("imam_name",showList[position])
               // intent.putExtra("total_chapter",showList[position].total_chapters)
                //intent.putExtra("title",showList[position].title)
                startActivity(intent)
            }
        })


        binding?.rvConsultantTop?.adapter = adapter

    }






}