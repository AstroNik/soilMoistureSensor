package com.example.soilmoisturesensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signup.setOnClickListener{ view: View ->
            startActivity(Intent(applicationContext, SignUp::class.java))
        }

        login.setOnClickListener{ view: View ->
            if (userName.text.toString().trim().length == 0){
                errorUserName.text = "Enter User Name"
            }else{
                errorUserName.text =""
                if (password.text.toString().trim().length == 0){
                    errorPassword.text = "Enter Password"
                }else{
                    errorPassword.text = ""
                    //Connect to database and fetch data
                    startActivity(Intent(applicationContext, Home::class.java))
                }
            }

        }


    }
}
