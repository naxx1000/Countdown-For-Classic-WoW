package com.rakiwow.classiccountdown

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {

    val timer: Timer = Timer()
    val scheduledService: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    lateinit var updateHandle: ScheduledFuture<*>

    lateinit var cooldownView: CooldownView
    var viewIsCreated: Boolean = false
    lateinit var ctx: Context

    //Handles all the time left of a cooldown
    val timeManager: TimeManager = TimeManager()

    var percentBetweenStartAndRelease: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ctx = this

        //TODO make landscape layout
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
                        ctx, 0f, itemImageView.measuredWidth.toFloat(),
                        0f, itemImageView.measuredHeight.toFloat(), percentBetweenStartAndRelease
                    )
                    main_layout.addView(cooldownView) // Add the view to the main constraint layout.

                    viewIsCreated = true
                }

                // Use ScheduledExecutorService, start it with scheduleAtFixedRate and cancel in onPause
                //https://stackoverflow.com/a/10347233/11878095
                val updater = Runnable {
                    updateTime()
                    cooldownView.y = itemImageView.y // Set position of the view to match the
                    cooldownView.x = itemImageView.x // itemImageView.
                }

                updateHandle = scheduledService.scheduleAtFixedRate(updater, 0, 1000, TimeUnit.MILLISECONDS)

                itemImageView.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })
    }

    override fun onPause() {
        updateHandle.cancel(true)
        super.onPause()
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

        //Change UI on UI thread
        runOnUiThread {
            cooldownView.invalidate()
        }
    }

    private fun changeDaysLeftText() {
        //Change text depending on if game/phase is released or not
        var daysLeftString: String
        if(timeManager.getPercentageTillRelease() < 360){
            daysLeftTextView.textSize = 38f
            daysLeftString = "${timeManager.getDays()} days\n${timeManager.getHours()} hrs\n${timeManager.getMinutes()} m\n${timeManager.getSeconds()}"
        }else{
            daysLeftTextView.textSize = 18f
            daysLeftString = "Dire Maul\nhas released!"
        }
        daysLeftTextView.text = daysLeftString
    }
}