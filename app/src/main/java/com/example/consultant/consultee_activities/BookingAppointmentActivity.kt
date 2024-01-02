package com.example.consultant.consultee_activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultee.AdapterSetBookTime
import com.example.consultant.bottom_navigation.BottomNavConsultee
import com.example.consultant.databinding.ActivityBookingAppointmentBinding
import com.example.consultant.model_classes_consultee.ModelSetBookTime
import com.example.consultant.progress_dialog.ProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingAppointmentActivity : AppCompatActivity() {
    private lateinit var consultantId: String
    lateinit var consultantName: String
    val db = FirebaseFirestore.getInstance()
    private lateinit var currentUserId: String
    private lateinit var mAuth: FirebaseAuth
    var bookingDate: String? = null
    private var selectedTime: String? = null
    lateinit var tvGoBackHome: TextView
    val loader = ProgressDialog(this)


    lateinit var binding: ActivityBookingAppointmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        consultantId = intent.getStringExtra("id") ?: ""
        consultantName = intent.getStringExtra("Consultant name") ?: ""
        mAuth = FirebaseAuth.getInstance()

        binding?.calenderView?.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            bookingDate = "$dayOfMonth-${month + 1}-$year"
        })

        onClick()
        showBookingTime()
        initTopBar()
    }

    private fun onClick() {
        binding?.btnConfirmBooking?.setOnClickListener {
            loader.showDialog()
            confirmBooking()
        }

        binding.topbarHome.ivImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun confirmBooking() {

        selectedTime = (binding?.rvSetTime?.adapter as? AdapterSetBookTime)?.getSelectedTime()

        if (CheckAllFields()) {

            if (currentUserId != null) {
                val userId = currentUserId

                val currentDateTime = Date()
                val bookingDateTime = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US ).parse("$bookingDate $selectedTime")

                // Compare the booking date and time with the current date and time
                val isValidBookingDateTime = bookingDateTime.after(currentDateTime) || bookingDateTime == currentDateTime

                // Check if the booking date and time is valid
                if (!isValidBookingDateTime) {
                    Toast.makeText(this, "Invalid booking date or time. Please select a current or future date and time.", Toast.LENGTH_SHORT).show()
                    return
                }


                // Check if user has already made a booking for the consultant
                db.collection("bookings").whereEqualTo("userId", userId)
                    .whereEqualTo("Consultant Id", consultantId)
                    .whereEqualTo("Booking day", bookingDate)
                    .whereEqualTo("BookingTime", selectedTime).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // User has already made a booking for the same consultant, day, and time
                            Toast.makeText(this,"You have already made a booking for this shop at this time",Toast.LENGTH_SHORT).show()
                        } else {
                            // Check if the selected time slot is already booked by another customer
                            db.collection("bookings").whereEqualTo("Consultant Id", consultantId)
                                .whereEqualTo("Booking day", bookingDate)
                                .whereEqualTo("BookingTime", selectedTime).get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        // The selected time slot is already booked by another customer
                                        Toast.makeText(   this,  "This time slot is already booked, please choose another one", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // User has not made a booking for the shop and the time slot is available, so create a new booking document
                                        val booking = hashMapOf(
                                            "userId" to userId,
                                            "Booking day" to bookingDate,
                                            "BookingTime" to selectedTime,
                                            "Status" to "Pending",
                                            "Consultant Id" to consultantId,
                                            "Consultant Name" to consultantName,
                                        )

                                        db.collection("bookings").add(booking)
                                            .addOnSuccessListener { documentRef ->
                                                loader.dialogDismiss()
                                                val dialog = BottomSheetDialog(this)
                                                val view = layoutInflater.inflate(
                                                    R.layout.activity_booking_success_message, null
                                                )
                                                dialog.setContentView(view)
                                                dialog.setCancelable(false)
                                                dialog.show()
                                                tvGoBackHome =
                                                    dialog.findViewById<TextView>(R.id.tvGoBackHome)!!
                                                tvGoBackHome.setOnClickListener {
                                                    val intent =
                                                        Intent(this, BottomNavConsultee::class.java)
                                                    startActivity(intent)
                                                }
                                            }.addOnFailureListener { exception ->
                                                loader.dialogDismiss()
                                                Log.e(
                                                    ContentValues.TAG,
                                                    "Error adding booking to Firestore",
                                                    exception
                                                )
                                            }
                                    }
                                }.addOnFailureListener { exception ->
                                    loader.dialogDismiss()
                                    Log.e(
                                        ContentValues.TAG,
                                        "Error checking if time slot is available",
                                        exception
                                    )
                                }
                        }
                    }.addOnFailureListener { exception ->
                        loader.dialogDismiss()
                        Log.e(
                            ContentValues.TAG,
                            "Error checking if user has made a booking",
                            exception
                        )
                    }
            }
        }
    }

    private fun showBookingTime() {
        val db = FirebaseFirestore.getInstance()
        val shopDocRef = db.collection("clinics").document(consultantId)

        shopDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val docId = document.id
                    val openTime = document.getString("OpenTime") ?: ""
                    val closeTime = document.getString("CloseTime") ?: ""

                    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val openTimeLocal = formatter.parse(openTime)
                    val closeTimeLocal = formatter.parse(closeTime)

                    val timeList = ArrayList<ModelSetBookTime>()
                    val calendar = Calendar.getInstance()

                    if (openTimeLocal != null && closeTimeLocal != null) {
                        calendar.time = openTimeLocal

                        while (calendar.time.before(closeTimeLocal)) {
                            val time = formatter.format(calendar.time)
                            val bookTime = ModelSetBookTime(docId, time)
                            timeList.add(bookTime)
                            calendar.add(Calendar.HOUR_OF_DAY, 1)
                        }

                        val adapter = AdapterSetBookTime()
                        adapter.set_Data(timeList)
                        binding?.rvSetTime?.adapter = adapter
                    } else {
                        Log.e(ContentValues.TAG, "Error parsing open and close time")
                    }
                } else {
                    Log.e(ContentValues.TAG, "Shop document not found")
                }
            } else {
                Log.e(ContentValues.TAG, "Error getting shop details", task.exception)
            }
        }
    }


    private fun CheckAllFields(): Boolean {
        if (bookingDate.isNullOrEmpty()) { // // Check if user has selected a date
            loader.dialogDismiss()
            Toast.makeText(this, "Please select a booking date", Toast.LENGTH_SHORT).show()
            return false
        }



        if (selectedTime.isNullOrEmpty()) {
             loader.dialogDismiss()
            Toast.makeText(this, "Please select booking time", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.setText("Booking")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
    }


}