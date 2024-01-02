package com.example.consultant.user_auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.consultant.R
import com.example.consultant.databinding.ActivityForgetPasswordBinding
import com.example.consultant.progress_dialog.ProgressDialog
import com.google.firebase.auth.FirebaseAuth

class  ForgetPasswordActivity : AppCompatActivity() {
    lateinit var binding:ActivityForgetPasswordBinding
    lateinit var email:String
    val loader = ProgressDialog(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTopBar()
        onClick()
    }

    private fun onClick() {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            onBackPressed()
        }

        binding.topbarHome.ivImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding?.tvSubmit?.setOnClickListener {
             email = binding?.etEmailAddress?.text.toString()
            loader.showDialog()

            if (email.isEmpty()) {
                loader.dialogDismiss()
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                loader.dialogDismiss()
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            } else {
                loader.dialogDismiss()
                forgetPassword()
            }
        }

    }

    private fun forgetPassword() {

        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loader.dialogDismiss()
                    Toast.makeText(this, "Check your email to reset password", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    loader.dialogDismiss()
                    val error = task.exception?.message ?: "Unknown error"
                    Log.e("PasswordReset", "Error: $error")
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Forget Password")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
    }
}