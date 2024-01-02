package com.example.consultant.adapter_classes_consultant

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.R
import com.example.consultant.model_classes_consultant.ModelConsultantAppointments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdapterConsultantAppointments(val context: Context): RecyclerView.Adapter<AdapterConsultantAppointments.ViewHolder>() {
    val currentUser= FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterConsultantAppointments.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_appointments, parent, false)
        return AdapterConsultantAppointments.ViewHolder(view)
    }

    lateinit var arrlist: ArrayList<ModelConsultantAppointments>
    fun set_Data(list: ArrayList<ModelConsultantAppointments>) {
        arrlist = list
    }


    override fun onBindViewHolder(holder: AdapterConsultantAppointments.ViewHolder, position: Int) {
        val appointment = arrlist[position]
        holder.tvCustomerName.text = appointment.CustomerName
        holder.tvBookingDay.text = appointment.BookingDay

        holder.tvStatus.text = appointment.Status
        holder.tvTiming.text=appointment.BookingTime
        holder.appointmentId = appointment.appointmentId
        // Set accept and reject button visibility based on appointment status
        if (appointment.Status == "Pending") {
            holder.btnAccept.visibility = View.VISIBLE
            holder.btnReject.visibility = View.VISIBLE
            holder.tvStatus.visibility = View.GONE
            holder.tvCompletedAppointment.visibility = View.GONE
        } else if (appointment.Status == "Accepted") {
            holder.btnAccept.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvCompletedAppointment.visibility = View.VISIBLE
        } else {
            holder.btnAccept.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvCompletedAppointment.visibility = View.GONE
        }
        holder.btnAccept.setOnClickListener {
            // Update appointment status in database to "Accepted"
            val db = FirebaseFirestore.getInstance()
            if(currentUser!=null) {

                val appointmentRef = db.collection("bookings").document(holder.appointmentId!!)
                appointmentRef.update("Status", "Accepted")
                    .addOnSuccessListener {

                        holder.tvStatus.text = "Accepted"
                        holder.tvStatus.visibility= View.VISIBLE
                        holder.tvCompletedAppointment.visibility= View.VISIBLE
                        holder.btnAccept.visibility = View.GONE
                        holder.btnReject.visibility = View.GONE

                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error updating document", e)
                    }
            }
        }

        holder.btnReject.setOnClickListener {
            // Update appointment status in database to "Rejected"
            val db = FirebaseFirestore.getInstance()
            if (currentUser != null) {
                val appointmentRef = db.collection("bookings").document(holder.appointmentId!!)
                appointmentRef.update("Status", "Rejected")
                    .addOnSuccessListener {
                        // Update status text view

                        holder.tvStatus.text = "Rejected"
                        holder.tvStatus.visibility= View.VISIBLE
                        holder.btnAccept.visibility = View.GONE
                        holder.btnReject.visibility = View.GONE
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error updating document", e)
                    }
            }
        }

        holder.tvCompletedAppointment.setOnClickListener {
            holder.tvCompletedAppointment.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle("Appointment Completion")
                    .setMessage("Are you sure the appointment is completed?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        // Update appointment status in database to "Completed"
                        val db = FirebaseFirestore.getInstance()
                        if (currentUser != null) {
                            val shopId = currentUser
                            val appointmentRef = db.collection("bookings").document(holder.appointmentId!!)
                            appointmentRef.update("Status", "Completed")
                                .addOnSuccessListener {
                                    // Update status text view
                                    holder.tvStatus.text = "Completed"
                                    holder.tvStatus.visibility = View.VISIBLE
                                    // Hide completed button
                                    holder.tvCompletedAppointment.visibility = View.GONE
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error updating document", e)
                                }
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                alertDialog.show()
            }

        }
    }

    override fun getItemCount(): Int {
        return arrlist.size
    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item)

    {
        var appointmentId: String? = null
        val tvCustomerName=item.findViewById<TextView>(R.id.tvCustomerName)
        val tvBookingDay=item.findViewById<TextView>(R.id.tvBookingDay)
        val tvTiming=item.findViewById<TextView>(R.id.tvTiming)
        val btnAccept=item.findViewById<TextView>(R.id.btnAccept)
        val btnReject=item.findViewById<TextView>(R.id.btnReject)
        val tvStatus=item.findViewById<TextView>(R.id.tvStatusBooking)
        val tvCompletedAppointment=item.findViewById<TextView>(R.id.tvCompletedAppointment)
    }

}