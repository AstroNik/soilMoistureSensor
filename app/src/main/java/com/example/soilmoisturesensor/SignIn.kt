package com.example.soilmoisturesensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.cancel

class SignIn : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        login.setOnClickListener(View.OnClickListener { view-> signIn()

            //Save the data in the database, display a message that account has been created
        })

        goBack.setOnClickListener{
            finish()
        }

        resetPasswordLink.setOnClickListener{
            startActivity(Intent(this, ResetPassword::class.java))
        }
    }

    private fun signIn(){

        val email = email.text.toString()
        val pwd = password.text.toString()

        if(!email.isEmpty() && !pwd.isEmpty()){

            mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, OnCompleteListener { task ->

                if(task.isSuccessful){
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, Home::class.java))
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
}