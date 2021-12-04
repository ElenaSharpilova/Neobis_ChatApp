package android.example.neochatapp.activities

import android.content.Intent
import android.example.neochatapp.*
import android.example.neochatapp.adapters.MessagesAdapter
import android.example.neochatapp.databinding.ActivityChatBinding
import android.example.neochatapp.models.Chat
import android.example.neochatapp.models.Message
import android.example.neochatapp.models.User
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.HashMap

//const val TOPIC = "/topics/myTopic2"
class ChatActivity : AppCompatActivity() {
    lateinit var binding:ActivityChatBinding
    private var chat: Chat? = null
    private var user: User? = null
    private val myId = FirebaseAuth.getInstance().uid
    private val messageList = ArrayList<Message>()
    private val adapter by lazy { MessagesAdapter(this) }
    private var chatExist: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        user = intent.getSerializableExtra("user") as User?
        chat = intent.getSerializableExtra("chat") as Chat?

        supportActionBar?.title = user?.name

        if (chat == null) {
            val userIds: ArrayList<String> = ArrayList()
            userIds.add(user?.id.toString())
            userIds.add(myId.toString())
            if (!exist(userIds)) {
                chat = Chat()
                chat!!.userIds = userIds
                supportActionBar?.title = user?.name
            }
        } else if (user == null) {
            isHave()
            getMessages()
            initRecyclerView()
            FirebaseFirestore.getInstance().collection("users")
                .document(chat?.userIds?.get(0).toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    user = documentSnapshot.toObject(User::class.java)
                    assert(user != null)
                    supportActionBar?.title = user?.name
                }
        }

        //FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        binding.sendButton.setOnClickListener {
            onClickSend()
            //val message = binding.message.text.toString()
            //if(message.isNotEmpty()) {
            //    PushNotification(
            //        NotificationData(message),
            //        TOPIC
            //    ).also {
            //        sendNotification(it)
             //   }
            //}
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun exist(userIds: ArrayList<String>): Boolean {
        FirebaseFirestore.getInstance().collection("chats")
            .whereEqualTo("userIds", userIds)
            .get()
            .addOnSuccessListener { snapshots ->
                if (snapshots != null) {
                    chatExist = true
                    for (snapshot in snapshots) {
                        chat = snapshot.toObject(Chat::class.java)
                        chat?.id = snapshot.id
                    }
                }
            }
            .addOnFailureListener {
                chatExist = false
            }
        return chatExist
    }

    private fun isHave() {
        val ids = chat?.userIds as MutableList
        ids.remove(myId)
        val userName = ids[0]

        FirebaseFirestore.getInstance().collection("chats")
            .document(chat?.id.toString())
            .collection("messages")
            .whereEqualTo("senderId", userName)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (doc in it.result!!) {
                        doc.reference.update("isSeen", true)
                    }
                }
            }
    }

    private fun getMessages() {
        FirebaseFirestore.getInstance().collection("chats").document(chat?.id.toString())
            .collection("messages")
            .orderBy("time")
            .addSnapshotListener { value, error ->
                for (change in value!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> messageList.add(
                            change.document.toObject(Message::class.java)
                        )
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun initRecyclerView() {
        binding.apply {
            recyclerViewChat.layoutManager = LinearLayoutManager(this@ChatActivity)
            recyclerViewChat.adapter = adapter
            adapter.setMessage(messageList)
        }
    }

    private fun onClickSend() {
        val messageText = binding.message.text.toString()
        if (chat?.id == null) {
            createChat(messageText)
        }
        else {
            sendMessage(messageText)
        }
    }


    private fun createChat(text: String) {
        val map: MutableMap<String, Any> = HashMap()
        map["userIds"] = chat?.userIds!!
        FirebaseFirestore.getInstance().collection("chats")
            .add(map)
            .addOnSuccessListener { documentReference ->
                chat?.id = (documentReference.id)
                sendMessage(text)
                getMessages()
                initRecyclerView()
                startActivity(Intent(this, MainActivity::class.java))
            }
    }

    private fun sendMessage(text: String) {
        val map: MutableMap<String, Any> = HashMap()
        map["text"] = text
        map["senderId"] = myId.toString()
        map["isHave"] = false
        map["time"] = FieldValue.serverTimestamp()

        FirebaseFirestore.getInstance().collection("chats").document(chat?.id!!)
            .collection("messages")
            .add(map)
        binding.message.setText("")
        binding.recyclerViewChat.scrollToPosition(adapter.itemCount - 1)
    }

   /* private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }*/
}