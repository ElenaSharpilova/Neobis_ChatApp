package android.example.neochatapp

import android.content.Intent
import android.example.neochatapp.activities.ChatActivity
import android.example.neochatapp.activities.ContactsActivity
import android.example.neochatapp.activities.LoginActivity
import android.example.neochatapp.adapters.ChatAdapter
import android.example.neochatapp.databinding.ActivityMainBinding
import android.example.neochatapp.interfaces.RecyclerItemClicked
import android.example.neochatapp.models.Chat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), RecyclerItemClicked {
    lateinit var binding: ActivityMainBinding
    private val chatList: MutableList<Chat> = ArrayList()
    private val adapter by lazy { ChatAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        binding.floatingActionButton.setOnClickListener{
            startActivity(Intent(this, ContactsActivity::class.java))
        }
        getUserList()
        letChats()
    }

    private fun letChats() {
        val uid = FirebaseAuth.getInstance().uid
        FirebaseFirestore.getInstance().collection("chats")
            .whereArrayContains("userIds", uid.toString())
            .addSnapshotListener { value, error ->
                for (change: DocumentChange in value!!.documentChanges){
                    when(change.type){
                        DocumentChange.Type.ADDED -> run {
                            val chat: Chat = change.document.toObject(Chat::class.java)
                            chat.id = change.document.id
                            chatList.add(chat)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun getUserList() {
        binding.apply {
            recyclerViewMain.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerViewMain.adapter = adapter
            adapter.setUser(chatList)
        }

    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chat", chatList[position])
        startActivity(intent)
    }
}