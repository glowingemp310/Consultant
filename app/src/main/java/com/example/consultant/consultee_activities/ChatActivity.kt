package com.example.consultant.consultee_activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consultant.R
import com.example.consultant.adapter_classes_consultee.MessageAdapter
import com.example.consultant.databinding.ActivityChatBinding
import com.example.consultant.model_classes_consultee.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        firestore = FirebaseFirestore.getInstance()


        val senderUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val receiverUid = intent.getStringExtra("uid") ?: ""
        senderRoom = "$senderUid-$receiverUid"
        receiverRoom = "$receiverUid-$senderUid"

        binding.ivSendBtn.setOnClickListener {
            val messageText = binding.etMessageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = Message(messageText, senderUid)
                sendMessage(message,receiverRoom)
                binding?.etMessageBox?.text?.clear()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }


        initTopBar()
        loadMessages()
        onClickListener()
        setRvChatting()


    }


    private fun onClickListener()
    {
        binding?.topbarHome?.ivImageLeft?.setOnClickListener {
            onBackPressed()
        }


    }

    private fun initTopBar() {
        binding?.topbarHome?.tvTopBarContent?.text =intent.getStringExtra("name")
        binding?.topbarHome?.ivImageLeft?.setImageDrawable(resources.getDrawable(R.drawable.arrow_back))
        binding?.topbarHome?.ivImageRight?.visibility = View.GONE
        /* val image = intent.getStringExtra("image")
         if (image != null) {
             binding?.topbarHome?.ivImageLeft?.let { Glide.with(this).load(image).into(it) }
         }*/
        binding?.topbarHome?.ivImageRight?.setVisibility(View.GONE)
    }

    private fun loadMessages() {
        firestore.collection("chats").document(senderRoom)
            .collection("messages")
            .orderBy("sequenceNumber")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "Error fetching messages: ", exception)
                    return@addSnapshotListener
                }

                messageList.clear()
                snapshot?.documents?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    message?.let { messageList.add(it) }
                }
                messageAdapter.notifyDataSetChanged()
                scrollToLastMessage()
            }
    }





    private fun scrollToLastMessage() {
        val lastItemPosition = messageList.size - 1
        if (lastItemPosition >= 0) {
            binding?.rvChatting?.smoothScrollToPosition(lastItemPosition)
        }
    }


    private fun sendMessage(message: Message, receiverRoom: String) {
        val senderMessageCollection = firestore.collection("chats").document(senderRoom)
            .collection("messages")
        val receiverMessageCollection = firestore.collection("chats").document(receiverRoom)
            .collection("messages")

        val timestamp = System.currentTimeMillis()

        senderMessageCollection
            .orderBy("sequenceNumber", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lastSequenceNumber = if (!querySnapshot.isEmpty) {
                    val lastMessage = querySnapshot.documents[0]
                    lastMessage.getLong("sequenceNumber") ?: 0
                } else {
                    0
                }
                val sequenceNumber = lastSequenceNumber + 1

                val senderMessage = hashMapOf(
                    "message" to message.message,
                    "senderUid" to message.senderUid,
                    "timestamp" to timestamp,
                    "sequenceNumber" to sequenceNumber
                )

                senderMessageCollection.add(senderMessage)
                    .addOnSuccessListener { senderDocumentReference ->
                        val senderMessageId = senderDocumentReference.id
                        senderDocumentReference.update("messageId", senderMessageId)
                            .addOnSuccessListener {
                                val receiverMessage = hashMapOf(
                                    "message" to message.message,
                                    "senderUid" to message.senderUid,
                                    "timestamp" to timestamp,
                                    "sequenceNumber" to sequenceNumber
                                )

                                receiverMessageCollection.add(receiverMessage)
                                    .addOnSuccessListener {
                                        binding.etMessageBox.text.clear()
                                    }
                                    .addOnFailureListener { error ->
                                        Log.e(TAG, "Error sending message to receiver: ", error)
                                    }
                            }
                            .addOnFailureListener { error ->
                                Log.e(TAG, "Error updating sender's chat: ", error)
                            }
                    }
                    .addOnFailureListener { error ->
                        Log.e(TAG, "Error sending message: ", error)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching last message: ", exception)
            }
    }



    private fun setRvChatting()
    {
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        binding.rvChatting.layoutManager = LinearLayoutManager(this)
        binding.rvChatting.adapter = messageAdapter
    }

    companion object {
        const val TAG = "ChatActivity"
    }
}