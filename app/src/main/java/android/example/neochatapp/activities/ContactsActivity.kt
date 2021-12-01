package android.example.neochatapp.activities

import android.content.Intent
import android.example.neochatapp.adapters.ContactsAdapter
import android.example.neochatapp.databinding.ActivityContactsBinding
import android.example.neochatapp.interfaces.RecyclerItemClicked
import android.example.neochatapp.models.User
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.R
import java.util.ArrayList

class ContactsActivity : AppCompatActivity(), RecyclerItemClicked {
    lateinit var binding: ActivityContactsBinding
    private val list: MutableList<User> = ArrayList()
    private val adapter by lazy { ContactsAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initList()
        getContacts()
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("user", list[position])
        startActivity(intent)
    }

    private fun initList() {
        binding.apply {
            recyclerViewContacts.layoutManager = LinearLayoutManager(this@ContactsActivity)
            recyclerViewContacts.adapter = adapter
            adapter.setUser(list)
        }
    }

    private fun getContacts(){
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener {
                for (snapshot in it){
                    val user: User? = snapshot.toObject(User::class.java)
                    if(user != null) {
                        user.id = snapshot.id
                    }
                    if (FirebaseAuth.getInstance().uid != user?.id) {
                        list.add(user!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

}