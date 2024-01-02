package com.example.consultant.consultant_fragments

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
import com.example.consultant.adapter_classes_consultant.AdapterChatConsultant
import com.example.consultant.databinding.FragmentConsultantChatBinding
import com.example.consultant.model_classes_consultee.ModelChatUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList
import java.util.Locale

class ConsultantChatFragment : Fragment() {
    var binding: FragmentConsultantChatBinding? = null
    val userList = ArrayList<ModelChatUsers>()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var adapter: AdapterChatConsultant? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentConsultantChatBinding?>(inflater,R.layout.fragment_consultant_chat, container, false)
        return binding?.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRecycleView()
        fetchUserList()

        initTopBar()

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
        val filteredNames = java.util.ArrayList<ModelChatUsers>()

        for (s in userList ) {
            if (s.name?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.getDefault())) == true) {
                //adding the element to filtered list
                filteredNames.add(s)
            }
        }

        adapter?.filterList(filteredNames)
    }



    private fun fetchUserList() {
        binding?.rvListConsultee?.layoutManager = LinearLayoutManager(requireContext())
        val db = FirebaseFirestore.getInstance()
        val usersCollectionRef = db.collection("users")

        // Clear the user list before fetching new data
        userList.clear()

        usersCollectionRef.whereEqualTo("profession", "Consultee")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userImage = document.getString("profileImage") ?: ""
                    val userName = document.getString("full name") ?: ""

                    if (userName!=null && userImage!=null) {
                        val customer = ModelChatUsers(userImage, userName)
                        userList.add(customer)
                    }
                }
                if (isAdded) {
                    adapter = AdapterChatConsultant(requireContext(), userList)
                    binding?.rvListConsultee?.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ChatFragment", "Failed to fetch customers", exception)
            }
    }





    private fun setRecycleView()
    {
        adapter = AdapterChatConsultant(requireContext(), userList)
        binding?.rvListConsultee?.layoutManager= LinearLayoutManager(requireContext())
        binding?.rvListConsultee?.adapter = adapter
    }


}