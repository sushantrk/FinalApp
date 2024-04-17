package com.ud.mylastproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.ud.mylastproject.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.Register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.Login.setOnClickListener {
            if (checking()) {
                val email = binding.Email.text.toString()
                val password = binding.Password.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
//                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this, "login Successful ", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Wrong Details", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter the Details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checking(): Boolean {
        if (binding.Email.text.toString().trim { it <= ' ' }.isNotEmpty()
            && binding.Password.text.toString().trim { it <= ' ' }.isNotEmpty()
        ) {
            return true
        }
        return false
    }
}