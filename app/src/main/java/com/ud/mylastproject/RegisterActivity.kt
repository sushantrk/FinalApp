package com.ud.mylastproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.service.autofill.UserData
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ud.mylastproject.databinding.ActivityRegisterBinding


import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore


class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore


//        binding.Continue.setOnClickListener {
//            if (checking()) {
//                val email = binding.EmailRegister.text.toString()
//                val password = binding.PasswordRegister.text.toString()
//                val name = binding.Name.text.toString()
//                val phone = binding.Phone.text.toString()
//
//
//                val user = hashMapOf(
//                    "Name" to name,
//                    "Phone" to phone,
//                    "email" to email
//                )
//                val Users = db.collection("USERS")
//                val query =Users.whereEqualTo("email",email).get()
//                    .addOnSuccessListener { tasks: QuerySnapshot ->
//                        if(tasks.isEmpty)
//                        {
//                            auth.createUserWithEmailAndPassword(email,password)
//                                .addOnCompleteListener(this){
//                                        task->
//                                    if(task.isSuccessful)
//                                    {
//                                        Users.document(email).set(user)
//
//                                        val intent=Intent(this,LoginActivity::class.java)
//                                        intent.putExtra("email",email)
//                                        startActivity(intent)
//                                        finish()
//                                    }
//                                    else
//                                    {
//                                        Toast.makeText(this,"Authentication Failed", Toast.LENGTH_LONG).show()
//                                    }
//                                }
//                        }
//                        else
//                        {
//                            Toast.makeText(this,"User Already Registered", Toast.LENGTH_LONG).show()
//                            val intent= Intent(this,MainActivity::class.java)
//                            startActivity(intent)
//                        }
//                    }
//            }
//            else{
//                Toast.makeText(this,"Enter the Details", Toast.LENGTH_LONG).show()
//            }
//        }


        binding.Continue.setOnClickListener {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
            register()
            Toast.makeText(this, "executed", Toast.LENGTH_SHORT).show()

        }

    }


    private fun register() {
        val email = binding.EmailRegister.text.toString()
        val password = binding.PasswordRegister.text.toString()
        val name = binding.Name.text.toString()
        val phone = binding.Phone.text.toString()
        Toast.makeText(this, "registerr ", Toast.LENGTH_SHORT).show()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    saveUserData( name,email,phone)
                    Toast.makeText(this, "Successfully Signed Up ", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Account already exists, Login", Toast.LENGTH_SHORT).show()
                }
//                progressBar.visibility = View.GONE
            }


    }

    private fun saveUserData(name: String, email: String,phone:String) {
        // Save user details in UserDetails collection
        val userDetails = UserData(name, email,phone)
        val userDetailsRef = firestore.collection("USERS").document(email).set("userDetails")

            .addOnSuccessListener {
                // User details saved successfully
                // Save user authentication status in SharedPreferences
                val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.putBoolean("flag", true)
                editor.putString("name", name)
                editor.putString("email", email)
                editor.putString("phone", phone)
                editor.apply()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }

    }


    private fun checking(): Boolean {
        if (binding.Name.text.toString().trim { it <= ' ' }.isNotEmpty()
            && binding.Phone.text.toString().trim { it <= ' ' }.isNotEmpty()
            && binding.EmailRegister.text.toString().trim { it <= ' ' }.isNotEmpty()
            && binding.PasswordRegister.text.toString().trim { it <= ' ' }.isNotEmpty()
        ) {
            return true
        }
        return false
    }
}