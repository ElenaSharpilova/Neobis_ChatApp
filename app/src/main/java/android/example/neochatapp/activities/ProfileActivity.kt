package android.example.neochatapp.activities

import android.content.Intent
import android.example.neochatapp.MainActivity
import android.example.neochatapp.R
import android.example.neochatapp.databinding.ActivityProfileBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        binding.btnSave.setOnClickListener{
            val name = binding.editName.text.toString()
            if (TextUtils.isEmpty(name)) {
                binding.editName.error = "Введите имя"
                return@setOnClickListener
            }
            val users = hashMapOf<String, Any>()
            users["name"] = name
            val userId = FirebaseAuth.getInstance().uid
            if (userId != null) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .set(users)
                    .addOnCompleteListener {
                        if (it.isSuccessful) startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
            }
        }
    }
}