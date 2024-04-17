package com.ud.mylastproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ud.mylastproject.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    lateinit var binding:FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentUserBinding.inflate(inflater,container,false)

        val name=arguments?.getString("name")
        binding.name.text = name.toString()

        val phone=arguments?.getString("phone")
        binding.phone.text = phone.toString()

        val email=arguments?.getString("email")
        binding.emailLog.text = email.toString()

       return binding.root//inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//added
        super.onViewCreated(view, savedInstanceState)

        binding.logout.setOnClickListener{
            binding.name.text=""
            binding.phone.text=""
            binding.emailLog.text=""
            requireActivity().run{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    }




    companion object {
        @JvmStatic
        fun newInstance() = UserFragment()
    }
}