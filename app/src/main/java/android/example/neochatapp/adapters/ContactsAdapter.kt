package android.example.neochatapp.adapters

import android.example.neochatapp.R
import android.example.neochatapp.databinding.ListUserBinding
import android.example.neochatapp.interfaces.RecyclerItemClicked
import android.example.neochatapp.models.User
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(private val itemClicker: RecyclerItemClicked) :
    RecyclerView.Adapter<ContactsAdapter.ItemHolder>() {

    private var listUsers = listOf<User>()
    fun setUser(list: List<User>) {
        this.listUsers = list
        notifyDataSetChanged()
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListUserBinding.bind(itemView)

        fun bind(user: User) = with(binding) {
            nameUser.text = user.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from((parent.context)).inflate(R.layout.list_user, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(listUsers[position])
        holder.itemView.setOnClickListener {
            itemClicker.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

}