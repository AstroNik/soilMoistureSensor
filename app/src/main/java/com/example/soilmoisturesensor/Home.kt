package com.example.soilmoisturesensor

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerlayout: DrawerLayout
    private lateinit var navigationView:NavigationView
    private lateinit var toolbar: Toolbar

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerlayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)

        val Toggle = ActionBarDrawerToggle(
            this,drawerlayout,toolbar,0,0
        )

        drawerlayout.addDrawerListener(Toggle)
        Toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        mAuth = FirebaseAuth.getInstance()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.Dashboard->{
                startActivity(Intent(applicationContext, Home::class.java))
            }
            R.id.Game->{
                startActivity(Intent(applicationContext, Game::class.java))
            }
            R.id.Logout->{
                mAuth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Successfully Log out", Toast.LENGTH_LONG).show()
            }
        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }
}
