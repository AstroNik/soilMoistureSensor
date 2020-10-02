package com.example.soilmoisturesensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        button_changeEmail.setOnClickListener{
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_holder, UpdateEmailFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        button_changePassword.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_holder, UpdatePasswordFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }
}