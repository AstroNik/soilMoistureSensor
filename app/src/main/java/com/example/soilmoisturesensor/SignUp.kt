package com.example.soilmoisturesensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        cancel.setOnClickListener{ view: View ->
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        ok.setOnClickListener{view: View ->
            //Save the data in the database, display a message that account has been created
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }
}
