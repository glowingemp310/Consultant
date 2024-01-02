package com.example.consultant.adapter_classes_consultant

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

class AdapterChatConsultant(val context: Context, private var userList: List<ModelChatUsers>):
    RecyclerView.Adapter<AdapterChatConsultant.ViewHolder>() {

    val firestore= FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_list_chat, parent, false)
        return ViewHolder(itemView)
    }

    fun filterList(filterdNames: ArrayList<ModelChatUsers>) {
        this.userList = filterdNames
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterChatConsultant.ViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.name.text = currentUser.name
        Glide.with(holder.image).load(currentUser.image).into(holder.image)
        holder.itemView.setOnClickListener {
            val userName = currentUser.name
            firestore.collection("users")
                .whereEqualTo("full name", userName)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val userId = documentSnapshot.id
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("name", currentUser.name)
                        intent.putExtra("image", currentUser.image)
                        intent.putExtra("uid", userId)
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
        val name=itemView.findViewById<TextView>(R.id.tvName)
        val image=itemView.findViewById<ImageView>(R.id.ivProfile)
    }
}