package com.example.consultant.adapter_classes_consultee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.R
import com.example.consultant.model_classes_consultee.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList:ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ItemSend=1
    val ItemReceive=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_send_message, parent, false)
            return SentMessageViewHolder(view)
        }
        else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_receive_message, parent, false)
            return ReceiveMessageViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage=messageList[position]
        if(holder.javaClass==SentMessageViewHolder::class.java)

        {

            val viewHolder=holder as SentMessageViewHolder
            holder.sendMessage.text=currentMessage.message
        }

        else
        {
            val viewHolder=holder as ReceiveMessageViewHolder
            holder.receiveMessage.text=currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage=messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderUid))
            return ItemSend

        else
            return ItemReceive
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val sendMessage=itemView.findViewById<TextView>(R.id.tvSendMessage)
    }

    class ReceiveMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val receiveMessage=itemView.findViewById<TextView>(R.id.tvResponse)
    }
}