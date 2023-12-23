package com.example.consultant.bottom_navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.consultant.R

import com.example.consultant.consultee_fragments.ConsulteeChatFragment
import com.example.consultant.consultee_fragments.ConsulteeHomeFragment
import com.example.consultant.consultee_fragments.ConsulteeProfileFragment
import com.example.consultant.databinding.ActivityBottomNavConsulteeBinding

class BottomNavConsultee : AppCompatActivity() {
    lateinit var binding:ActivityBottomNavConsulteeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBottomNavConsulteeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding?.bottomNavigationView?.itemIconTintList = null
        replaceFragment(ConsulteeHomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.home -> replaceFragment(ConsulteeHomeFragment())
                R.id.chat -> replaceFragment(ConsulteeChatFragment())
                R.id.profile -> replaceFragment(ConsulteeProfileFragment())

                else -> {

                }

            }

            true

        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()


    }
}