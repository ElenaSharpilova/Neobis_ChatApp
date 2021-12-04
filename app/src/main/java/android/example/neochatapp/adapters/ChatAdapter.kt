package android.example.neochatapp.adapters

import android.example.neochatapp.R
import android.example.neochatapp.databinding.ListUserBinding
import android.example.neochatapp.databinding.SingleChatBinding
import android.example.neochatapp.interfaces.RecyclerItemClicked
import android.example.neochatapp.models.Chat
import android.example.neochatapp.models.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatAdapter(private val itemClicker: RecyclerItemClicked):
    RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

    private var listChat = listOf<Chat>()
    fun setUser(list: List<Chat>) {
        this.listChat = list
        notifyDataSetChanged()
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName = ""
        val binding = SingleChatBinding.bind(itemView)

        fun bind(chat: Chat) {
            val idsUsers = chat.userIds as ArrayList
            idsUsers.remove(FirebaseAuth.getInstance().uid)
            userName = idsUsers[0]

            FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener {
                    for (snapshot in it) {
                        if (snapshot.id == userName) {
                            binding.txtView.text = snapshot["name"].toString()
                        }
                    }
                }
        }

        fun isHave(chat: Chat) {
            val ids = chat.userIds as ArrayList
            userName = ids[0]
            var newMessage = 0
            binding.count.visibility = View.GONE
            FirebaseFirestore.getInstance().collection("chats")
                .document(chat.id.toString())
                .collection("messages")
                .whereEqualTo("senderId", userName)
                .get()
                .addOnSuccessListener {
                    for (snapshot in it) {
                        val message: Message = snapshot.toObject(Message::class.java)
                        if (!message.isHave) {
                            newMessage += 1
                        }
                    }
                    if(newMessage > 0){
                        binding.count.text = newMessage.toString()
                        binding.count.visibility = View.VISIBLE
                    }
                }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from((parent.context)).inflate(R.layout.single_chat, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(listChat[position])
        holder.isHave(listChat[position])
        holder.binding.layout.setOnClickListener {
            itemClicker.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listChat.size
    }

}