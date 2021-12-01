package android.example.neochatapp.adapters

import android.example.neochatapp.R
import android.example.neochatapp.databinding.ListUserBinding
import android.example.neochatapp.interfaces.RecyclerItemClicked
import android.example.neochatapp.models.Chat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ChatAdapter(private val itemClicker: RecyclerItemClicked):
    RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

    private var listChat = listOf<Chat>()
    fun setUser(list: List<Chat>) {
        this.listChat = list
        notifyDataSetChanged()
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListUserBinding.bind(itemView)

        fun bind(chat: Chat) {
            val idsUsers = chat.userIds as ArrayList
            val userName = idsUsers[0]

            FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener {
                    for (snapshot in it) {
                        if (snapshot.id == userName) {
                            binding.nameUser.text = snapshot["name"].toString()
                        }
                    }
                }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from((parent.context)).inflate(R.layout.list_user, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(listChat[position])
        holder.binding.layout.setOnClickListener {
            itemClicker.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listChat.size
    }

}