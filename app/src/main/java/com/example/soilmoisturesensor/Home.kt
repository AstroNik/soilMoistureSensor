package com.example.soilmoisturesensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
    }

    fun loadSensorDetail(view : View){
        startActivity(Intent(applicationContext, SensorDetail::class.java))
    }

    fun logOut(view: View){
        mAuth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(this, "Successfully Log out", Toast.LENGTH_LONG).show()
    }

    fun loadGame(view : View){
        startActivity(Intent(applicationContext, Game::class.java))
    }
}
