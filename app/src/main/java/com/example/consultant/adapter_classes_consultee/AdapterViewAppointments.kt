package com.example.consultant.adapter_classes_consultee

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.R
import com.example.consultant.model_classes_consultee.ModelViewAppointments
import com.google.firebase.firestore.FirebaseFirestore

class AdapterViewAppointments(val context: Context): RecyclerView.Adapter<AdapterViewAppointments.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewAppointments.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_consultee_appointment, parent, false)
        return AdapterViewAppointments.ViewHolder(view)
    }

    lateinit var arrlist: ArrayList<ModelViewAppointments>
    fun set_Data(list: ArrayList<ModelViewAppointments>) {
        arrlist = list
    }

    @SuppressLint("LongLogTag")
    override fun onBindViewHolder(holder: AdapterViewAppointments.ViewHolder, position: Int) {
        holder.clinicName.text=arrlist[position].clinicName
        holder.bookingDay.text=arrlist[position].bookingDay
        holder.timeSlot.text=arrlist[position].timeSlot
        holder.statusBooking.text=arrlist[position].statusBooking

        if (arrlist[position].statusBooking == "Pending") {
            holder.tvCompletedAppointment.visibility = View.GONE
            holder.tvCancelBooking.visibility= View.VISIBLE
        } else if(arrlist[position].statusBooking == "Accepted") {
            holder.tvCompletedAppointment.visibility = View.VISIBLE
            holder.tvCancelBooking.visibility= View.GONE
        }
        else
        {
            holder.tvCompletedAppointment.visibility = View.VISIBLE
            holder.tvCancelBooking.visibility= View.GONE
        }


        holder.infoCall.setOnClickListener {

            val clinicName = holder.clinicName.text.toString()
            val db = FirebaseFirestore.getInstance()
            val shopsCollectionRef = db.collection("clinics")

            shopsCollectionRef.whereEqualTo("Clinic Name", clinicName)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val phoneNumber = documents.documents[0].getString("Phone no")
                        phoneNumber?.let {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:$phoneNumber")
                            context.startActivity(intent)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("AdapterAppointmentCustomer", "Failed to fetch shop data", exception)
                }
        }

        holder.tvCompletedAppointment.setOnClickListener {
            val shopName = arrlist[position].clinicName

            val dialog = AlertDialog.Builder(context)
                .setTitle("Complete Appointment")
                .setMessage("Are you sure about completing this appointment?")
                .setPositiveButton("Completed") { dialogInterface, _ ->
                    // User confirmed the completion, proceed with updating the status

                    val db = FirebaseFirestore.getInstance()
                    val clinicCollectionRef = db.collection("clinics")
                    val bookingsCollectionRef = db.collection("bookings")

                    clinicCollectionRef.whereEqualTo("Clinic Name", shopName).get()
                        .addOnSuccessListener { clinicDocuments ->
                            if (!clinicDocuments.isEmpty) {
                                val clinicId = clinicDocuments.documents[0].id
                                // Query the bookings collection based on shopId and update the status to "Completed"
                                bookingsCollectionRef.whereEqualTo("Consultant Id", clinicId).get()
                                    .addOnSuccessListener { bookingDocuments ->
                                        for (bookingDocument in bookingDocuments) {
                                            val appointmentId = bookingDocument.id

                                            val newStatus = "Completed" // Update to the desired completed status

                                            bookingsCollectionRef.document(appointmentId)
                                                .update("Status", newStatus)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Appointment marked as completed", Toast.LENGTH_SHORT).show()
                                                    // holder.tvCompletedAppointment.visibility = View.VISIBLE
                                                    arrlist[position].statusBooking = newStatus
                                                    notifyDataSetChanged()
                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.e("AdapterAppointmentCustomer", "Failed to update appointment status", exception)
                                                    // Handle the failure scenario
                                                }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("AdapterAppointmentCustomer", "Failed to fetch bookings data", exception)
                                        // Handle the failure scenario
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("AdapterAppointmentCustomer", "Failed to fetch shop data", exception)
                            // Handle the failure scenario
                        }

                    dialogInterface.dismiss()
                }
                .setNegativeButton("Not completed") { dialogInterface, _ ->
                    // User cancelled the completion, update the status to "Not Completed"

                    val db = FirebaseFirestore.getInstance()
                    val shopsCollectionRef = db.collection("clinics")
                    val bookingsCollectionRef = db.collection("bookings")

                    // Query the shops collection based on shop name to get the corresponding shopId
                    shopsCollectionRef.whereEqualTo("Clinic Name", shopName).get()
                        .addOnSuccessListener { clinicDocuments ->
                            if (!clinicDocuments.isEmpty) {
                                val consultantId = clinicDocuments.documents[0].id

                                // Query the bookings collection based on shopId and update the status to "Not Completed"
                                bookingsCollectionRef.whereEqualTo("Consultant Id", consultantId).get()
                                    .addOnSuccessListener { bookingDocuments ->
                                        for (bookingDocument in bookingDocuments) {
                                            val appointmentId = bookingDocument.id

                                            val newStatus ="Not Completed" // Update to the desired not completed status

                                            bookingsCollectionRef.document(appointmentId)
                                                .update("Status", newStatus)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context,
                                                        "Appointment marked as not completed", Toast.LENGTH_SHORT).show()
                                                    // holder.tvCompletedAppointment.visibility =View.GONE
                                                    arrlist[position].statusBooking = newStatus
                                                    notifyDataSetChanged()
                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.e("AdapterAppointmentCustomer", "Failed to update appointment status", exception)
                                                    // Handle the failure scenario
                                                }
                                        }
                                    }
                                    .addOnFailureListener { exception -> Log.e("AdapterAppointmentCustomer", "Failed to fetch bookings data", exception)
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("AdapterAppointmentCustomer", "Failed to fetch shop data", exception)

                        }
                    dialogInterface.dismiss()
                }
                .create()

            dialog.show()

        }


        holder.tvCancelBooking.setOnClickListener {
            val shopName = holder.clinicName.text.toString()
            val bookingDay = arrlist[position].bookingDay
            val bookingTime = arrlist[position].timeSlot

            val dialog = AlertDialog.Builder(context)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes") { dialogInterface, _ ->
                    // User confirmed the cancellation, proceed with deleting the appointment

                    val db = FirebaseFirestore.getInstance()
                    val bookingsCollectionRef = db.collection("bookings")
                    val shopsCollectionRef = db.collection("clinics")

                    // Query the shops collection based on shop name to get the corresponding shopId
                    shopsCollectionRef.whereEqualTo("Clinic Name", shopName)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val shopId = documents.documents[0].id

                                // Query the bookings collection based on shopId and delete the booking document
                                bookingsCollectionRef.whereEqualTo("Consultant Id", shopId)
                                    .whereEqualTo("Booking day", bookingDay)
                                    .whereEqualTo("BookingTime", bookingTime)
                                    .get()
                                    .addOnSuccessListener { bookingDocuments ->
                                        for (bookingDocument in bookingDocuments) {
                                            bookingsCollectionRef.document(bookingDocument.id)
                                                .delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Appointment cancel successfully",
                                                        Toast.LENGTH_SHORT).show()
                                                    val position = holder.adapterPosition
                                                    if (position != RecyclerView.NO_POSITION) {
                                                        arrlist.removeAt(position)
                                                        notifyDataSetChanged()
                                                    }
                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.e("AdapterAppointmentCustomer", "Failed to delete booking document", exception)
                                                    // Handle the failure scenario
                                                }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("AdapterAppointmentCustomer", "Failed to fetch bookings data", exception)
                                        // Handle the failure scenario
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("AdapterAppointmentCustomer", "Failed to fetch shop data", exception)
                            // Handle the failure scenario
                        }

                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface, _ ->
                    // User cancelled the cancellation, do nothing
                    dialogInterface.dismiss()
                }
                .create()

            dialog.show()
        }


    }


    override fun getItemCount(): Int {
        return arrlist.size
    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item)
    {
        val clinicName=item.findViewById<TextView>(R.id.tvClinicName)
        val bookingDay=item.findViewById<TextView>(R.id.tvBookingDay)
        val timeSlot=item.findViewById<TextView>(R.id.tvbookingTime)
        val statusBooking=item.findViewById<TextView>(R.id.tvStatusBooking)
        val infoCall=item.findViewById<TextView>(R.id.infoCall)
        val tvCancelBooking=item.findViewById<TextView>(R.id.tvCancelBooking)
        val tvCompletedAppointment=item.findViewById<TextView>(R.id.tvCompletedAppointment)

    }
}