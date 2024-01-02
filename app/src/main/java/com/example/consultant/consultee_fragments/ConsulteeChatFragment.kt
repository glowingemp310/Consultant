package com.example.consultant.consultee_fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultee.AdapterChatUsers
import com.example.consultant.databinding.FragmentConsulteeChatBinding
import com.example.consultant.databinding.FragmentConsulteeHomeBinding
import com.example.consultant.model_classes_consultee.ModelChatUsers
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ConsulteeChatFragment : Fragment() {
    var binding: FragmentConsulteeChatBinding?=null
    val userList = ArrayList<ModelChatUsers>()
    private var adapter: AdapterChatUsers? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentConsulteeChatBinding?>(inflater,R.layout.fragment_consultee_chat, container, false)
        return binding?.getRoot()


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTopBar()
        fetchBarberShops()

        binding?.etSearchChat?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })

    }


    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Chat")
        binding?.topbarHome?.ivImageLeft?.setVisibility(View.GONE)
        binding?.topbarHome?.ivImageRight?.setVisibility(View.GONE)
    }

    private fun filter(text:String) {
        val filterdNames = java.util.ArrayList<ModelChatUsers>()

        for (s in userList ) {
            if (s.name?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.getDefault())) == true) {
                //adding the element to filtered list
                filterdNames.add(s)
            }
        }

        adapter?.filterList(filterdNames)
    }



    private fun fetchBarberShops() {
        binding?.rvListConsultant?.layoutManager = LinearLayoutManager(requireContext())
        val db = FirebaseFirestore.getInstance()
        val shopsCollectionRef = db.collection("clinics")
        shopsCollectionRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val consultantImage = document.getString("image") ?: ""
                val consultantName = document.getString("Consultant Name") ?: ""

                if (consultantName != null && consultantImage != null) {
                    val barberShop = ModelChatUsers(consultantImage, consultantName)
                    userList.add(barberShop)
                }

            }
            if(isAdded) {
                adapter= AdapterChatUsers(requireContext(), userList)
                binding?.rvListConsultant?.adapter = adapter
            }
        }
            .addOnFailureListener { exception ->
                Log.e("ChatFragment", "Failed to fetch barber shops", exception)
            }
    }



}