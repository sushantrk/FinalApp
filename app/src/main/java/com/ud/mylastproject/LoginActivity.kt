package com.ud.mylastproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ud.mylastproject.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)

        binding.Register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.Login.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        if (checkInput()) {
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        saveLoginStatus(email)
                        finish()
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Login Failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkInput(): Boolean {
        return binding.Email.text.toString().isNotEmpty() && binding.Password.text.toString().isNotEmpty()
    }

    private fun saveLoginStatus(email: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("flag", true)
        editor.putString("email", email)

        // Fetch additional user data from Firestore
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("USERS").document(email)
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    val phone = document.getString("phone")

                    // Store additional user data in shared preferences
                    editor.putString("name", name)
                    editor.putString("phone", phone)
                    editor.apply()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "User data not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@LoginActivity,
                    "Failed to fetch user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}
