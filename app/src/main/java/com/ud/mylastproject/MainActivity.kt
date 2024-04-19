package com.ud.mylastproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ud.mylastproject.databinding.ActivityMainBinding
import android.content.Context
import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore


    private lateinit var NAME: String
    private lateinit var PHONE: String
    private lateinit var EMAIL: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val isLogin = sharedPref.getString("Email", "1")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomBar.selectedItemId = R.id.nav_home
        inflateFragment(HomeFragment.newInstance())

        binding.bottomBar.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.nav_map -> {
                    inflateFragment(MapsFragment())
                }

                R.id.nav_home -> {
                    inflateFragment(HomeFragment.newInstance())
                }

                R.id.nav_user -> {

                    if (isLogin == "1") {
                        val email = intent.getStringExtra("email")
                        if (email != null) {

                            with(sharedPref.edit())
                            {
                                putString("Email", email)
                                apply()
                            }
                            setText(email)
                        } else {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        setText(isLogin)
                    }
//                    inflateFragment(UserFragment.newInstance())
                }

                R.id.nav_dashboard -> {
                    inflateFragment(MapFragment.newInstance())
                }
            }
            true
        }


    }


    private fun setText(email: String?) {
        db = FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("USERS").document(email).get()
                .addOnSuccessListener { tasks ->
                    val fragment = UserFragment()
                    NAME = tasks.get("Name").toString()
                    PHONE = tasks.get("Phone").toString()
                    EMAIL = tasks.get("email").toString()
                    val bundle = Bundle()
                    bundle.putString("name", NAME)
                    bundle.putString("phone", PHONE)
                    bundle.putString("email", EMAIL)

                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
                }
        }


    }

    private fun inflateFragment(newInstance: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newInstance)
        transaction.commit()
    }


}