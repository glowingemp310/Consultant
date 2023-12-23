package com.example.consultant.consultant_activities

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.consultant.R
import com.example.consultant.bottom_navigation.BottomNavConsultant
import com.example.consultant.databinding.ActivityRegisterClinicBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar
import java.util.HashMap
import java.util.UUID

class RegisterClinicActivity : AppCompatActivity() {
    lateinit var binding:ActivityRegisterClinicBinding
    var openTimeSet = false
    var closeTimeSet = false
    var occupation=""
    private val occupations = arrayOf("Heart Specialist", "Eye Specialist", "Dermatologists", "Dentist","Allergist","Endocrinologists","Gastroenterologists","Oncologists","Family Physicians")

    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterClinicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore=FirebaseFirestore.getInstance()
        mAuth=FirebaseAuth.getInstance()

        onClick()
        initTopBar()
        initAdapter()
    }

    private fun onClick() {
        binding?.btnOpenTimeSelect?.setOnClickListener {
            openTimeSet=true
            shopOpenTime()

        }


        binding?.btnCloseTimeSelect?.setOnClickListener {
            closeTimeSet=true
            shopCloseTime()
        }

        binding?.ivConsultantImage?.setOnClickListener {
            choosePhotoOptions()
        }

        binding.btnRegister.setOnClickListener {
            registerOrUpdateShop()

        }
    }

    private fun initAdapter()
    {
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, occupations
        )
        binding.spSelectOccupation.adapter = adapter
        binding.spSelectOccupation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    occupation = occupations[position].lowercase()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }


            }
    }

    private fun registerOrUpdateShop() {
        val shopRef = firestore.collection("clinics").document(mAuth.currentUser?.uid!!)
        shopRef.get()
            .addOnSuccessListener { documentSnapshot ->
               // loader.dialogDismiss()
                if (documentSnapshot.exists()) {
                    val clinic = documentSnapshot.data

                    // Create a new map to store the updated shop details
                    val updatedDetail = HashMap<String, Any>()

                    // Check if each field has been changed, and update the map accordingly
                    if (binding?.etName?.text.toString() != clinic?.get("Consultant Name")) {
                        updatedDetail["Consultant Name"] = binding?.etName?.text.toString()
                    }

                    if (binding?.etClinicName?.text.toString() != clinic?.get("Clinic Name")) {
                        updatedDetail["Clinic Name"] = binding?.etClinicName?.text.toString()
                    }

                    if (binding?.etAddress?.text.toString() != clinic?.get("Address")) {
                        updatedDetail["Address"] = binding?.etAddress?.text.toString()
                    }

                    if (binding?.etAboutYourSelf?.text.toString() != clinic?.get("About")) {
                        updatedDetail["About"] = binding?.etAboutYourSelf?.text.toString()
                    }
                    if (binding?.etPhoneNo?.text.toString() != clinic?.get("Phone no")) {
                        updatedDetail["Phone no"] = binding?.etPhoneNo?.text.toString()
                    }

                    if (binding?.etCnic?.text.toString() != clinic?.get("Cnic")) {
                        updatedDetail["Cnic"] = binding?.etCnic?.text.toString()
                    }


                    if (binding?.tvOpenTime?.text.toString() != clinic?.get("OpenTime")) {
                        updatedDetail["OpenTime"] = binding?.tvOpenTime?.text.toString()
                    }
                    if (binding?.tvCloseTime?.text.toString() != clinic?.get("CloseTime")) {
                        updatedDetail["CloseTime"] = binding?.tvCloseTime?.text.toString()
                    }


                    // Update the shop image if a new one was selected
                    val consultantImage = binding?.ivConsultantImage?.drawable
                    if (consultantImage != null && consultantImage is BitmapDrawable) {
                        val bitmap = consultantImage.bitmap
                        val uri = bitmapToFile(bitmap)

                        // Store the URI as a string in the database
                        updatedDetail["image"] = uri.toString()
                    }

                    // Only update the document if there are changes
                    if (updatedDetail.isNotEmpty()) {
                        shopRef.set(updatedDetail, SetOptions.merge()).addOnSuccessListener {
                           // loader.dialogDismiss()
                            Toast.makeText(this, "Shop details updated successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, BottomNavConsultant::class.java)
                            startActivity(intent)
                            finish()
                        }
                            .addOnFailureListener {
                               // loader.dialogDismiss()
                                Toast.makeText(this, "Error updating shop details", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                       // loader.dialogDismiss()
                        Toast.makeText(this,"No changes were made to the shop details", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, BottomNavConsultant::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else {
                    // Create new shop

                    if (checkAllFields()) {
                       // loader.showDialog()
                        createNewShop()
                    }
                }
            }
    }


    private fun createNewShop() {

        val newClinic = hashMapOf(
            "Consultant Name" to binding.etName.text.toString(),
            "Clinic Name" to binding.etClinicName.text.toString(),
            "Address" to binding.etAddress.text.toString(),
            "Occupation" to occupation,
            "About" to binding.etAboutYourSelf.text.toString(),
            "Phone no" to binding.etPhoneNo.text.toString(),
            "Cnic" to binding.etCnic.text.toString(),
            "OpenTime" to binding.tvOpenTime.text.toString(),
            "CloseTime" to binding.tvCloseTime.text.toString(),
        )

        // Update the shop image if a new one was selected
        val consultantImage = binding?.ivConsultantImage?.drawable
        if (consultantImage != null && consultantImage is BitmapDrawable) {
            val bitmap = consultantImage.bitmap
            val imageRef = storageRef.child("consultant_images/${mAuth.currentUser?.uid!!}/${UUID.randomUUID()}.jpg")

            // Create a byte array output stream to write the bitmap data into a byte array
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Upload the byte array to Firebase Storage
            val uploadTask = imageRef.putBytes(data)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
               // loader.dialogDismiss()
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    newClinic["image"] = downloadUri.toString()
                    firestore.collection("clinics").document(mAuth.currentUser?.uid!!)
                        .set(newClinic)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Clinic registered successfully",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, BottomNavConsultant::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                           // loader.dialogDismiss()
                            Toast.makeText(this, "Error registering clinic", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    //loader.dialogDismiss()
                    Toast.makeText(this, "Error uploading consultant image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            //loader.dialogDismiss()
            Toast.makeText(this, "Please select an image for the shop", Toast.LENGTH_SHORT).show()
        }
    }


    private fun bitmapToFile(bitmap: Bitmap?): File {
        if (bitmap == null) {
            throw IllegalArgumentException("Bitmap cannot be null")
        }
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    // User took a photo using the camera
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding?.ivConsultantImage?.setImageBitmap(imageBitmap)

                }
                1 -> {
                    // User selected an image from the gallery
                    val imageUri = data?.data
                    binding?.ivConsultantImage?.setImageURI(imageUri)


                }
            }
        }
    }

    private fun choosePhotoOptions()
    {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
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



    private fun shopOpenTime()
    {
        val currentTime = Calendar.getInstance()
        val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                // Update the text of the Show Time Picker button with the selected time
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hour = if (selectedHour > 12) selectedHour - 12 else selectedHour
                val selectedTime = String.format("%02d:%02d %s", hour, selectedMinute, amPm)
                //showTimePickerButton.text = selectedTime
                binding?.tvOpenTime?.text = selectedTime
            },
            hourOfDay,
            minute,
            false
        )

        // Show the TimePickerDialog
        timePickerDialog.show()
    }

    private fun shopCloseTime()
    {
        val currentTime = Calendar.getInstance()
        val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                // Update the text of the Show Time Picker button with the selected time
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hour = if (selectedHour > 12) selectedHour - 12 else selectedHour
                val selectedTime = String.format("%02d:%02d %s", hour, selectedMinute, amPm)
                //showTimePickerButton.text = selectedTime
                binding?.tvCloseTime?.text = selectedTime
            },
            hourOfDay,
            minute,
            false
        )

        // Show the TimePickerDialog
        timePickerDialog.show()
    }



    private fun checkAllFields():Boolean
    {
        if(binding.etName.text!!.isEmpty()) {
            binding.etName.error = "Name field is empty"
            return false
        }

        if(binding.etClinicName.text!!.isEmpty()) {
            binding.etClinicName.error = "Provide your clinic name"
            return false
        }

        if(binding.etAddress.text!!.isEmpty()) {
            binding.etAddress.error="Provide your clinic address"
            return false
        }

        if(binding.etAboutYourSelf.text!!.isEmpty()) {
            binding.etAboutYourSelf.error="Provide some detail about yourself"
            return false
        }

        if(binding.etPhoneNo.text!!.isEmpty()) {
            binding.etPhoneNo.error="Phone no. should not be empty"
            return false
        }else if (binding.etPhoneNo.length()!= 11) {
            binding.etPhoneNo.error = "Phone no. should have exactly 11 digits"
            return false
        }

        if(binding.etCnic.text!!.isEmpty()) {
            binding.etClinicName.error = "Cnic is required"
            return false
        }
        if(!openTimeSet) {
            binding?.tvOpenTime?.error="Please add shop open time"
            return false
        }
        else {
            binding?.tvOpenTime?.error = null // clear error message
        }

        if(!closeTimeSet) {
            binding?.tvCloseTime?.error = "Please add shop close time"
            return false
        }
        else {
            binding?.tvCloseTime?.error = null // clear error message
        }
        return true
    }


    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Register")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.menu))
        //binding?.topbarHome?.ivImageRight?.setVisibility(View.GONE)
    }

}