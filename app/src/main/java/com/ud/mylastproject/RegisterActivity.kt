package com.ud.mylastproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ud.mylastproject.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.Continue.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = binding.EmailRegister.text.toString()
        val password = binding.PasswordRegister.text.toString()
        val name = binding.Name.text.toString()
        val phone = binding.Phone.text.toString()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserData(name, email, phone)
                    Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserData(name: String, email: String, phone: String) {
        val userDetails = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone
        )

        firestore.collection("USERS").document(email)
            .set(userDetails)
            .addOnSuccessListener {
                val pref: SharedPreferences =
                    getSharedPreferences("login", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.putBoolean("flag", true)
                editor.putString("name", name)
                editor.putString("email", email)
                editor.putString("phone", phone)
                editor.apply()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to save user data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
