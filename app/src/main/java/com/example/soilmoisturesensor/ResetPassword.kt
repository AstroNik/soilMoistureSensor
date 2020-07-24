package com.example.soilmoisturesensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPassword : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        mAuth = FirebaseAuth.getInstance()

        resetPassword.setOnClickListener{
            var userEmail = emailResetPassword.text.toString()

            if (userEmail.isEmpty()){
                Toast.makeText(this, "Insert valid email address", Toast.LENGTH_SHORT).show()
            }else{
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SignIn::class.java))
                    }
                    else{

                        Toast.makeText(this, "Error occured: "+ it.exception?.message , Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        goBackSignIn.setOnClickListener{
            finish()
        }
    }
}