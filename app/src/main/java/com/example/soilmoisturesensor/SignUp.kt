package com.example.soilmoisturesensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        ok.setOnClickListener(View.OnClickListener { view-> register()

            //Save the data in the database, display a message that account has been created
        })

        cancel.setOnClickListener{

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun register(){

        val mName = fullName.text.toString()
        val mEmail = emailAddress.text.toString()
        val mPwd = password1.text.toString()
        val mPwd1 = password2.text.toString()

        if(!mName.isEmpty() && !mEmail.isEmpty() && !mPwd.isEmpty()){

            if(mPwd.equals(mPwd1)) {

                mAuth.createUserWithEmailAndPassword(mEmail, mPwd)
                    .addOnCompleteListener(this, OnCompleteListener { task ->

                        if (task.isSuccessful) {
                            //val user = mAuth.currentUser
                            //val uid = user!!.uid
                            // mDB.child(uid).child("Names").setValue(mName)

                            /*****-------- DB Code -------------*****/

                            /*****-------- DB Code -------------*****/

                            startActivity(Intent(this, MainActivity::class.java))

                            Toast.makeText(this, "SignUp Successful", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error :(", Toast.LENGTH_LONG).show()

                        }
                    })
            }
            else{

                Toast.makeText(this, "Password doesnot match", Toast.LENGTH_LONG).show()
            }
        }
        else{

            Toast.makeText(this, "Please fill values", Toast.LENGTH_SHORT).show()
        }
    }
}
