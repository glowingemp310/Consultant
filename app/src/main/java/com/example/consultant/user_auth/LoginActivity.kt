package com.example.consultant.user_auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.consultant.bottom_navigation.BottomNavConsultant
import com.example.consultant.bottom_navigation.BottomNavConsultee
import com.example.consultant.databinding.ActivityLoginBinding
import com.example.consultant.progress_dialog.ProgressDialog
import com.example.consultant.utils.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var dbRef: FirebaseFirestore
    val loader = ProgressDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth=FirebaseAuth.getInstance()
        dbRef=FirebaseFirestore.getInstance()

        onCLick()
    }

    private fun onCLick() {

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterUserActivity::class.java)
            startActivity(intent)
        }

        if(SharedPreference.shared.isConsulteeLogin==true)
        {
            val intent = Intent(this, BottomNavConsultee::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.btnLogin.setOnClickListener {
            val email=binding.etEmail.text.toString()
            val password=binding.etPassword.text.toString()

            if(checkAllFields()) {

                loader.showDialog()
                loginUser(email,password)
            }
        }

        binding.tvForgetPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
    }


    private fun loginUser(email: String, password: String) {
        // Sign in the user with email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If the authentication is successful, get the user info from the database
                    dbRef.collection("users").whereEqualTo("email", email).get()
                        .addOnSuccessListener { querySnapshot ->
                            loader.dialogDismiss()
                            if (!querySnapshot.isEmpty) {
                                val document = querySnapshot.documents[0]
                                val profession = document.getString("profession")
                                when (profession) {
                                    "Consultant" -> {
                                        SharedPreference.shared.isConsultantLogin=true
                                        val intent = Intent(this, BottomNavConsultant::class.java)
                                        startActivity(intent)
                                    }
                                    "Consultee" -> {
                                        SharedPreference.shared.isConsulteeLogin=true
                                        val intent = Intent(this, BottomNavConsultee::class.java)
                                        startActivity(intent)
                                    }
                                }
                            } else {
                                loader.dialogDismiss()
                                Toast.makeText(applicationContext, "User not found.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    loader.dialogDismiss()
                    Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun checkAllFields(): Boolean {

        if (binding.etPassword.text.isEmpty() || !binding.etEmail.text.toString()
                .matches(Patterns.EMAIL_ADDRESS.toRegex())
        ) {
            binding.etEmail.error = "Invalid Email address"
            return false
        }

        if (binding.etPassword.length() == 0) {
            binding.etPassword.error = "This field is required"
            return false
        }

        return true
    }



}