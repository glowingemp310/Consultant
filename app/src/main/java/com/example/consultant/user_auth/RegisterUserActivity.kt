package com.example.consultant.user_auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.consultant.R
import com.example.consultant.bottom_navigation.BottomNavConsultant
import com.example.consultant.bottom_navigation.BottomNavConsultee
import com.example.consultant.databinding.ActivityRegisterUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUserActivity : AppCompatActivity() {
    lateinit var binding:ActivityRegisterUserBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var isAllFieldChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth=FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()

        onClick()
    }

    private fun onClick() {
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {


            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val genderRole = when (binding.radioGroupGender.checkedRadioButtonId) {
                R.id.radio_button_male -> "Male"
                R.id.radio_button_female -> "Female"
                else -> ""
            }

            val professionRole = when (binding.radioGroupJoining.checkedRadioButtonId) {
                R.id.rbConsultant -> "Consultant"
                R.id.rbConsultee -> "Consultee"
                else -> ""
            }

            isAllFieldChecked = checkAllFields()


            if(isAllFieldChecked)
            {
                createUser(fullName,email,password,genderRole,professionRole)
            }


        }
    }


    private fun createUser(fullName:String, email:String, password:String, genderRole:String, professionRole:String)
    {

        // If user does not exist, create a new user account with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If user account creation is successful, add user info to Firestore
                    val user = hashMapOf(
                        "full name" to fullName,
                        "email" to email,
                        "gender" to genderRole,
                        "profession" to professionRole
                    )

                    firestore.collection("users").document(mAuth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                           // loader.dialogDismiss()
                            when (professionRole) {
                                "Consultant" -> {
                                    val intent = Intent(this, BottomNavConsultant::class.java)
                                    startActivity(intent)
                                }
                                "Consultee" -> {
                                    val intent = Intent(this, BottomNavConsultee::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                        .addOnFailureListener {
                          //  loader.dialogDismiss()
                            // If user info could not be added to Firestore, show an error message
                            Toast.makeText(applicationContext, "Failed to add user info.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    val exception = task.exception
                    Log.e("FirebaseAuth", "Error: ${exception?.message}")
                    Toast.makeText(applicationContext, "Error: ${exception?.message}", Toast.LENGTH_SHORT).show()
                }
                }
            }










    private fun checkAllFields():Boolean
    {
        if(binding.etFullName.text.isNullOrEmpty())
        {
            binding.etFullName.error="Name should not be empty"
        }


        if ( !binding.etEmail.text.toString().matches(Patterns.EMAIL_ADDRESS.toRegex()))
        {
            binding.etEmail.error = "Invalid Email address"
            return false
        }

        if (binding.etPassword.text.isNullOrEmpty() || binding.etConfirmPassword.text.isNullOrEmpty()) {
            Toast.makeText(this, "Password fields cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.radioGroupGender.checkedRadioButtonId==-1) {
            Toast.makeText(applicationContext, "Please select your gender", Toast.LENGTH_SHORT).show()
            return false
        }


        if(binding.radioGroupJoining.checkedRadioButtonId==-1) {
            Toast.makeText(applicationContext, "Please select your profession", Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }
}