package com.example.soilmoisturesensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        login.setOnClickListener(View.OnClickListener {
                view -> signIn()
        })

        signup.setOnClickListener(View.OnClickListener {
                view->register()
        })


    }
    private fun signIn(){

        val email = email.text.toString()
        val pwd = password.text.toString()

        if(!email.isEmpty() && !pwd.isEmpty()){

            mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, OnCompleteListener { task ->

                if(task.isSuccessful){

                    startActivity(Intent(this, Home::class.java))
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show()
                }
            })
        }
        else
        {
            Toast.makeText(this, "Please fill values", Toast.LENGTH_SHORT).show()
        }


    }

    private fun register(){

        startActivity(Intent(this,SignUp::class.java ))

    }
}
