package com.example.consultant.bottom_navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.consultant.R
import com.example.consultant.consultant_fragments.ConsultantChatFragment
import com.example.consultant.consultant_fragments.ConsultantHomeFragment
import com.example.consultant.consultant_fragments.ConsultantProfileFragment
import com.example.consultant.databinding.ActivityBottomNavConsultantBinding

class BottomNavConsultant : AppCompatActivity() {
    lateinit var binding:ActivityBottomNavConsultantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBottomNavConsultantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding?.bottomNavigationView?.itemIconTintList = null
        replaceFragment(ConsultantHomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.home -> replaceFragment(ConsultantHomeFragment())
                R.id.chat -> replaceFragment(ConsultantChatFragment())
                R.id.profile -> replaceFragment(ConsultantProfileFragment())

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