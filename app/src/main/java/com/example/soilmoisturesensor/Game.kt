package com.example.soilmoisturesensor

import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class Game : AppCompatActivity() {
    var score: Int = 0
    var timesUp : Boolean = false
    var imageArray = ArrayList<ImageView>()
    var handler : Handler = Handler()
    var runnable : Runnable = Runnable {  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        score = 0
        imageArray = arrayListOf(imageView,imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8)
        hideImages()
        imageTree.setImageResource(R.drawable.plant_game)


        object : CountDownTimer(100000, 1000){
            override fun onFinish() {
                timesUp = true
                timeText.text = "Time's up "
            }

            override fun onTick(p0: Long) {
                timeText.text = "Time: " + p0/1000
            }
        }.start()

        if (timesUp){
            Toast.makeText(this, "Sorry, time is up", Toast.LENGTH_LONG).show()
        }
        if (score==100){
            Toast.makeText(this, "WINNER!!!", Toast.LENGTH_LONG).show()
        }
    }

    fun increaseScore(view: View) {
        score++
        scoreText.text = "Score: " + score
        when (score) {
            11 -> imageTree.setImageResource(R.drawable.plant_game_1)
            22 -> imageTree.setImageResource(R.drawable.plant_game_2)
            33 -> imageTree.setImageResource(R.drawable.plant_game_3)
            44 -> imageTree.setImageResource(R.drawable.plant_game_4)
            55 -> imageTree.setImageResource(R.drawable.plant_game_5)
            66 -> imageTree.setImageResource(R.drawable.plant_game_6)
            77 -> imageTree.setImageResource(R.drawable.plant_game_7)
            88 -> imageTree.setImageResource(R.drawable.plant_game_8)
            100 ->imageTree.setImageResource(R.drawable.plant_game_9)
            else -> {
            }
        }
    }

    fun hideImages(){
        runnable = object : Runnable{
            override fun run() {
                if (score == 100){
                    gridLayout2.visibility = View.INVISIBLE
                    scoreText.text = "Winner"
                    timeText.visibility = View.INVISIBLE
                }
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }
                val random = Random()
                val index = random.nextInt(8-0)
                imageArray[index].visibility = View.VISIBLE
                handler.postDelayed(runnable, 500)

                if (timesUp){
                    gridLayout2.visibility = View.INVISIBLE
                    timeText.text = "Time's up"
                }
            }
        }
        handler.post(runnable)

    }

}