package android.example.neochatapp.activities

import android.content.Intent
import android.example.neochatapp.R
import android.example.neochatapp.databinding.ActivityLoginBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var verificationCode: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.codeLayout.visibility = View.GONE
        binding.btnPhone.setOnClickListener {
            onClickLogin()
            binding.phoneLayout.visibility = View.GONE
            binding.codeLayout.visibility = View.VISIBLE
        }
        binding.btnCode.setOnClickListener {
            val code = binding.editTextCode.text.toString()
            if (code.isEmpty() || code.length < 6) {
                Toast.makeText(this@LoginActivity, "Please enter code", Toast.LENGTH_SHORT).show()
            }
            verifyCode(code)
        }
    }


    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Log.e("TAG", "onVerificationCompleted: ")
            val code = p0.smsCode
            if (code != null) {
                binding.editTextCode.setText(code)
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(this@LoginActivity, "${p0.message}", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationCode = p0
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationCode, code)
        signIn(credential)
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                finish()
            } else Toast.makeText(
                this@LoginActivity,
                "Authorization error ${it.exception?.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onClickLogin() {
        val number = binding.editTextPhone.text.toString()
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(number)       // верификация номера телефона
            .setTimeout(60L, TimeUnit.SECONDS) // Время на ввод кода
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }
}

