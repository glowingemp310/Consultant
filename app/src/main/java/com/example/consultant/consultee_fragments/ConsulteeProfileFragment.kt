package com.example.consultant.consultee_fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.consultant.R
import com.example.consultant.databinding.FragmentConsultantHomeBinding
import com.example.consultant.databinding.FragmentConsulteeProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class ConsulteeProfileFragment : Fragment() {

    lateinit var binding: FragmentConsulteeProfileBinding
    val currentUser= FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentConsulteeProfileBinding>(
            inflater, R.layout.fragment_consultee_profile, container, false
        )

        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        showUserProfile()
        initTopBar()
    }

    private fun onClick() {
        binding?.btnSaveChanges?.setOnClickListener {
           // loader.showDialog()
            updateProfile()
        }

        binding?.profilePic?.setOnClickListener {
            choosePhotoOptions()
        }
    }

    private fun choosePhotoOptions() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 0)
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 1)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    // User took a photo using the camera
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding?.profilePic?.setImageBitmap(imageBitmap)

                }
                1 -> {
                    // User selected an image from the gallery
                    val imageUri = data?.data
                    binding?.profilePic?.setImageURI(imageUri)


                }
            }
        }
    }


    private fun updateProfile() {
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId)

            val newName = binding?.etFullName?.text.toString()
            val newEmail = binding?.etMail?.text.toString()

            val updatedFields = mutableMapOf<String, Any>()
            if (newName!!.isNotEmpty()) {
                updatedFields["full name"] = newName
            }

            if (newEmail!!.isNotEmpty()) {
                updatedFields["email"] = newEmail
            }

            val profileImage = binding?.profilePic?.drawable
            if (profileImage != null && profileImage is BitmapDrawable) {
                val bitmap = profileImage.bitmap
                val imageRef = storageRef.child("profile_images/$userId.jpg")
                // Create a byte array output stream to write the bitmap data into a byte array
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val uploadTask = imageRef.putBytes(data)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        updatedFields["profileImage"] = downloadUri.toString()
                    }


                    db.collection("users").document(userId).update(updatedFields)
                        .addOnSuccessListener {
                            //loader.dialogDismiss()
                            // Update successful
                            Toast.makeText(requireContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show()
                            //val intent = Intent(requireContext(), Bott::class.java)
                            //startActivity(intent)

                        }

                        .addOnFailureListener { exception ->
                            //loader.dialogDismiss()
                            // Handle any errors that occur during the update
                            Toast.makeText(requireContext(), "Failed to save changes", Toast.LENGTH_SHORT).show()

                        }
                }
            } else {
              //  loader.dialogDismiss()
                // No profile picture selected, update the user's profile without image
                if (updatedFields.isEmpty()) {
                    Toast.makeText(requireContext(), "Enter a field to update", Toast.LENGTH_SHORT).show()
                    return
                }
                db.collection("users").document(userId)
                    .update(updatedFields)
                    .addOnSuccessListener {
                       // loader.dialogDismiss()
                        // Update successful
                        Toast.makeText(requireContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show()
                      //  val intent = Intent(requireContext(), MainFragmentNav::class.java)
                      //  startActivity(intent)
                    }
                    .addOnFailureListener { exception ->
                    //    loader.dialogDismiss()
                        // Handle any errors that occur during the update
                        Toast.makeText(requireContext(), "Failed to save changes", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun showUserProfile()
    {
        if(currentUser!=null) {
            val userId=currentUser.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if(document.exists()){
                        val fullname=document.getString("full name")
                        val emailAddress=document.getString("email")
                        val profileImageUrl = document.getString("profileImage")
                        binding?.tvName?.text=fullname
                        binding?.tvMail?.text=emailAddress
                        if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                            Picasso.get()
                                .load(profileImageUrl).into(binding?.profilePic)
                        }
                        else {
                            // Set a default profile image or a placeholder if no profile image is available
                            binding?.profilePic?.setImageResource(R.drawable.profile)
                        }


                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting user document: ", exception)
                }
        }
    }

    private fun initTopBar() {
        binding?.topbarProfile?.tvTopBarContent?.setText("Profile")
        binding?.topbarProfile?.ivImageRight?.setVisibility(View.GONE)
        binding?.topbarProfile?.ivImageLeft?.setVisibility(View.GONE)

    }





}