package android.example.neochatapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.example.neochatapp.R
import android.example.neochatapp.databinding.ReceiveMessageBinding
import android.example.neochatapp.databinding.SentMessageBinding
import android.example.neochatapp.models.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList

class MessagesAdapter(val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2


    private var messageList = ArrayList<Message>()
    fun setMessage(messageList: ArrayList<Message>) {
        this.messageList = messageList
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1){
            val view = LayoutInflater.from((parent.context)).inflate(R.layout.receive_message, parent, false)
            ViewHolderReceive(view)
        } else{
            val view = LayoutInflater.from((parent.context)).inflate(R.layout.sent_message, parent, false)
            ViewHolderSent(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.javaClass == ViewHolderSent::class.java) {
            val viewHolder = holder as ViewHolderSent
            viewHolder.bind(messageList[position])
        } else {
            val viewHolder = holder as ViewHolderReceive
            viewHolder.bind(messageList[position])
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    inner class ViewHolderSent(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = SentMessageBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bind(message: Message){
            binding.sentMessage.text = message.text
            val dateTime = Calendar.getInstance()
            dateTime.time = message.time.toDate()
            binding.sentTime.text = "${dateTime.get(Calendar.HOUR_OF_DAY)}:${dateTime.get(Calendar.MINUTE)}"
        }
    }

    inner class ViewHolderReceive(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ReceiveMessageBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bind(message: Message) {
            binding.receivedMessage.text = message.text
            val dateTime = Calendar.getInstance()
            dateTime.time = message.time.toDate()
            binding.receivedTime.text = "${dateTime.get(Calendar.HOUR_OF_DAY)}: ${dateTime.get(Calendar.MINUTE)}"
        }
    }

}