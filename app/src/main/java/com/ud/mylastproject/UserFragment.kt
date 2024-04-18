package com.ud.mylastproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ud.mylastproject.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE)

        val name = sharedPreferences.getString("name", "")
        val phone = sharedPreferences.getString("phone", "")
        val email = sharedPreferences.getString("email", "")

        binding.name.text = name
        binding.phone.text = phone
        binding.emailLog.text = email

        binding.logout.setOnClickListener {
            clearSharedPreferences()
            requireActivity().run {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun clearSharedPreferences() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserFragment()
    }
}
