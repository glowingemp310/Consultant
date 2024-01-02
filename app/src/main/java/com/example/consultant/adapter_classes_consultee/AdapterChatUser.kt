package com.example.consultant.adapter_classes_consultee

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consultant.R
import com.example.consultant.consultee_activities.ChatActivity
import com.example.consultant.model_classes_consultee.ModelChatUsers
import com.google.firebase.firestore.FirebaseFirestore

class AdapterChatUsers(val context: Context, private var userList: List<ModelChatUsers>):
    RecyclerView.Adapter<AdapterChatUsers.ViewHolder>() {

    val firestore= FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_list_chat, parent, false)
        return ViewHolder(itemView)
    }

    fun filterList(filterdNames: ArrayList<ModelChatUsers>) {
        this.userList = filterdNames
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterChatUsers.ViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.consultantName.text = currentUser.name
        Glide.with(holder.consultantImage).load(currentUser.image).into(holder.consultantImage)
        holder.itemView.setOnClickListener {
            val shopName = currentUser.name
            firestore.collection("clinics")
                .whereEqualTo("Consultant Name", shopName)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val shopId = documentSnapshot.id
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("name", currentUser.name)
                        intent.putExtra("image", currentUser.image)
                        intent.putExtra("uid", shopId)
                        context.startActivity(intent)
                    }
                }
                .addOnFailureListener { error ->
                    Log.e(ContentValues.TAG, "Error retrieving shop ID: ", error)
                }
        }
    }


    override fun getItemCount(): Int {
        return userList.size
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val consultantName=itemView.findViewById<TextView>(R.id.tvName)
        val consultantImage=itemView.findViewById<ImageView>(R.id.ivProfile)
    }
}