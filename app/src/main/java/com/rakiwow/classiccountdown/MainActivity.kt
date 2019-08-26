package com.rakiwow.classiccountdown

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {

    val timer: Timer = Timer()

    lateinit var cooldownView: CooldownView
    var viewIsCreated: Boolean = false
    lateinit var ctx: Context

    //Handles all the time left of a cooldown
    val timeManager: TimeManager = TimeManager()

    var percentBetweenStartAndRelease: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println(Date().time)
        ctx = this

        if(Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            }
        }

        //TODO make landscape layout
        //TODO Add comments
    }

    override fun onResume() {
        super.onResume()

        //Listen for when the itemImageView has been drawn. Then we can add the cooldown view on top
        itemImageView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if(!viewIsCreated){
                    // Create a cooldown view with same dimensions as the itemImageView
                    cooldownView = CooldownView(
                        ctx, 0f, itemImageView.width.toFloat(),
                        0f, itemImageView.height.toFloat(), percentBetweenStartAndRelease
                    )
                    main_layout.addView(cooldownView) // Add the view to the main constraint layout.

                    viewIsCreated = true
                }
                cooldownView.y = itemImageView.y // Set position of the view to match the
                cooldownView.x = itemImageView.x // itemImageView.

                //Starts the timer running code every second
                timer.scheduleAtFixedRate(0, 1000) {
                    updateTime()
                }

                itemImageView.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })
    }

    private fun updateTime() {
        //Update the timemanager to current time
        timeManager.updateNow()

        //Updates the days, hours, minutes and seconds left on top of the item view.
        runOnUiThread {
            changeDaysLeftText()
        }

        //Calculate percentage of time left to get the value for the cooldown arc
        cooldownView._arc = timeManager.getPercentageTillRelease()

        runOnUiThread {
            cooldownView.invalidate()
        }
    }

    private fun changeDaysLeftText() {
        var daysLeftString: String
        if(timeManager.getPercentageTillRelease() < 360){
            daysLeftTextView.textSize = 38f
            daysLeftString = "${timeManager.getDays()} days\n${timeManager.getHours()} hrs\n${timeManager.getMinutes()} m\n${timeManager.getSeconds()}"
        }else{
            daysLeftTextView.textSize = 18f
            daysLeftString = "What are you\nstill doing here?\n\nGo play\nWorld of Warcraft!"
        }
        daysLeftTextView.text = daysLeftString
    }
}