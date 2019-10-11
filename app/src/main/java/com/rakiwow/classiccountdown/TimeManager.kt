package com.rakiwow.classiccountdown

import java.util.*

class TimeManager{

    //Start date so the 'percentageTillRelease' function has reference to create a cooldown arc
    val randomlyChosenStartDate = 1570747597590L
    //The release date of Dire Maul (UTC) https://www.unixtimestamp.com/index.php
    val releaseDate = Date(1571115600000L)
    var now = Date()
    var millisTillClassic = releaseDate.time - now.time

    fun getSeconds() : Int{
        return ((millisTillClassic / 1000) % 60).toInt()
    }

    fun getMinutes() : Int{
        return ((millisTillClassic / (1000 * 60)) % 60).toInt()
    }

    fun getHours() : Int{
        return ((millisTillClassic / (1000 * 60 * 60)) % 24).toInt()
    }

    fun getDays() : Int{
        return ((millisTillClassic / (1000 * 60 * 60 * 24) % 24)).toInt()
    }

    //Update the time so that the functions above return the correct values.
    fun updateNow(){
        now = Date() //TODO is instantiating Date() every second avoidable?
        millisTillClassic = releaseDate.time - now.time
    }

    //Get the percentage of time elapsed since start date and release, then times 360 to get degrees.
    fun getPercentageTillRelease() : Float{
        return ((now.time - randomlyChosenStartDate).toFloat() / (releaseDate.time - randomlyChosenStartDate)) * 360
    }
}
