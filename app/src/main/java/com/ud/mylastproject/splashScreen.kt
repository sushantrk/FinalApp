package com.ud.mylastproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper


class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentStart = Intent(this,LoginActivity::class.java)
        val intentMain = Intent(this,MainActivity::class.java)

        val pref: SharedPreferences = getSharedPreferences("login", AppCompatActivity.MODE_PRIVATE)
        val flag : Boolean = pref.getBoolean("flag",false)

        val handler = Handler(Looper.getMainLooper())

        val runnable = Runnable{
            if(flag){
                startActivity(intentMain)
            }
            else {
                startActivity(intentStart)
            }
            finish()
        }
        handler.postDelayed(runnable,3000)

    }
}