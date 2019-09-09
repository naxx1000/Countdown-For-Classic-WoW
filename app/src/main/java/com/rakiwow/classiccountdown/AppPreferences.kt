package com.rakiwow.classiccountdown

import android.content.Context

val PREF_NAME = "preference"
val PREF_WIDGET_ID = "widgetId"

//Object for updating and retrieving id of widget or widgets
class AppPreferences(context: Context){

    val preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun updateWidgetIds(ids: MutableSet<String>){
        val editor = preference.edit()
        val newSet = mutableSetOf<String>()
        newSet.addAll(ids)
        editor.putStringSet(PREF_WIDGET_ID, newSet)
        editor.apply()
    }

    fun getWidgetIds() : MutableSet<String>{
        return preference.getStringSet(PREF_WIDGET_ID, hashSetOf())
    }
}