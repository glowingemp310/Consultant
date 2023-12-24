package com.example.consultant.consultant_fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.consultant.R
import com.example.consultant.consultant_activities.RegisterClinicActivity
import com.example.consultant.databinding.FragmentConsultantHomeBinding
import com.example.consultant.user_auth.LoginActivity
import com.example.consultant.utils.SharedPreference


class ConsultantHomeFragment : Fragment() {
    var binding:FragmentConsultantHomeBinding?=null
    lateinit var DrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentConsultantHomeBinding?>(inflater,R.layout.fragment_consultant_home, container, false)
        return binding?.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTopBar()
        onClick()
        //setupBarberListingRecycler()
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


}