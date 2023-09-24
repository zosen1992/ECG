package com.zosen.ecg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler(Looper.getMainLooper()).postDelayed(object : Runnable{
            override fun run() {

                val intent = Intent (applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        },2500)

    }
}