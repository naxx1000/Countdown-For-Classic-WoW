package com.rakiwow.classiccountdown

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
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

    var randomlyChosenStartDate = 1566429035979L//1566021497896L
    var releaseDate = Date(1566856800000)
    var date = Date()
    var millisTillClassic: Long = 0
    var timeTillClassic: Date = Date()

    var seconds: Int = 0
    var minutes: Int = 0
    var hours: Int = 0
    var days: Int = 0

    var percentBetweenStartAndRelease: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ctx = this

        //Sets the text for the item description below the hourglass
        changeItemDescriptionText()

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
        date = Date() //TODO is instantiating Date() every second avoidable?
        millisTillClassic = releaseDate.time - date.time
        timeTillClassic = Date(millisTillClassic)

        //Get the date in days, hours, minutes and seconds
        seconds = ((millisTillClassic / 1000) % 60).toInt()
        minutes = ((millisTillClassic / (1000 * 60)) % 60).toInt()
        hours = ((millisTillClassic / (1000 * 60 * 60)) % 24).toInt()
        days = ((millisTillClassic / (1000 * 60 * 60 * 24) % 24)).toInt()

        //Updates the days, hours, minutes and seconds left on top of the item view.
        changeDaysLeftText()

        //Calculate percentage of time left to get the value for the cooldown arc
        percentBetweenStartAndRelease =
            ((date.time - randomlyChosenStartDate).toFloat() / (releaseDate.time - randomlyChosenStartDate)) * 360
        cooldownView._arc = percentBetweenStartAndRelease
        runOnUiThread {
            cooldownView.invalidate()
        }
    }

    private fun changeDaysLeftText() {
        val daysLeftString = "$days days\n$hours hrs\n$minutes m\n$seconds"
        runOnUiThread {
            daysLeftTextView.text = daysLeftString
        }
    }

    private fun changeItemDescriptionText() {
        val countdownString =
            "<font color='${ContextCompat.getColor(
                this,
                R.color.epicQuality
            )}'>" + //Change font color
                    "Classic Hourglass</font><br>" + //The title of the item
                    "Soulbound<br>Unique<br>Trinket<br>" +
                    "<font color='${ContextCompat.getColor(this, R.color.uncommonQuality)}'>" +
                    "Use: Allows you to relive your past.</font><br>" +
                    "<font color='${ContextCompat.getColor(this, R.color.item_comment)}'>" +
                    "\"Property of Nozdormu\"</font>"
        dateTimeText.setText(
            HtmlCompat.fromHtml(countdownString, HtmlCompat.FROM_HTML_MODE_LEGACY),
            TextView.BufferType.SPANNABLE
        )
    }
}