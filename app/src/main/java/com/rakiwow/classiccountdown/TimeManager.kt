package com.rakiwow.classiccountdown

import java.util.*

class TimeManager{

    val randomlyChosenStartDate = 1566429035979L
    val releaseDate = Date(1566856800000)
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

    fun updateNow(){
        now = Date() //TODO is instantiating Date() every second avoidable?
        millisTillClassic = releaseDate.time - now.time
    }

    fun getPercentageTillRelease() : Float{
        return ((now.time - randomlyChosenStartDate).toFloat() / (releaseDate.time - randomlyChosenStartDate)) * 360
    }
}