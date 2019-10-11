package com.rakiwow.classiccountdown

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.util.*

const val COUNTDOWN_WIDGET_SYNC = "COUNTDOWN_WIDGET_SYNC"

//With help from CodeAndroid on Youtube to create a widget in Kotlin
class CountdownWidget : AppWidgetProvider() {

    lateinit var preference: AppPreferences

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        //Initialize an AppPreferences object
        if(!::preference.isInitialized){
            preference = AppPreferences(context)
        }
        val ids = preference.getWidgetIds()

        for (appWidgetId in appWidgetIds) {
            ids.add(appWidgetId.toString())
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        preference.updateWidgetIds(ids)
    }

    override fun onEnabled(context: Context) {
        val intent = Intent(context, CountdownWidget::class.java)
        intent.action = COUNTDOWN_WIDGET_SYNC
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        //Create instance of calendar, one minute in the future.
        val now = Calendar.getInstance()
        now.set(Calendar.SECOND, 0)
        now.add(Calendar.MINUTE, 1)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //From docs: Schedule a repeating alarm that has inexact trigger time requirements;
        // for example, an alarm that repeats every hour, but not necessarily at the top of every hour.
        alarmManager.setInexactRepeating(AlarmManager.RTC, now.timeInMillis, 60000, pendingIntent)
    }

    //Called when broadcast from pending intent is received.
    override fun onReceive(context: Context, intent: Intent?) {
        if(COUNTDOWN_WIDGET_SYNC == intent?.action){
            if(!::preference.isInitialized){
                preference = AppPreferences(context)
            }
            val ids = preference.getWidgetIds()
            for(id in ids){
                updateAppWidget(context, AppWidgetManager.getInstance(context), id.toInt())
            }
        }
        super.onReceive(context, intent)
    }

    //Companion object used to call its members, like using static methods in Java.
    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            //Create intent action with broadcast
            val intent = Intent(context, CountdownWidget::class.java)
            intent.action = COUNTDOWN_WIDGET_SYNC
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            val views = RemoteViews(context.packageName, R.layout.countdown_widget)

            //TimeManager is in charge of getting the seconds, minutes, hours and days until release.
            val timeManager = TimeManager()
            //TimeManager needs to update to current time.
            timeManager.updateNow()
            //Check if release hasn't happened yet.
            if(timeManager.getPercentageTillRelease() < 360){
                //Draw the cooldown arc with text in front of it.
                val bitmapArc = drawArc(context, timeManager.getPercentageTillRelease())
                val bitmapText = drawTimeLeft(context, timeManager.getDays().toString(), timeManager.getHours().toString(),
                    timeManager.getMinutes().toString())
                views.setImageViewBitmap(R.id.image_view_bitmap_cooldown, bitmapArc)
                views.setImageViewBitmap(R.id.image_view_bitmap_text, bitmapText)
                views.setOnClickPendingIntent(R.id.widget_image_view, pendingIntent)
            }else{
                //If game or phase is released, show READY text.
                views.setImageViewBitmap(R.id.image_view_bitmap_text, drawTextString(context, "READY"))
            }

            //Update the widget using updateAppWidget from the Widget Manager.
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        //Function that returns a bitmap with text for when the game or phases is released.
        fun drawTimeLeft(context: Context, days: String, hours: String, minutes: String) : Bitmap{
            val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val robotoFont = ResourcesCompat.getFont(context, R.font.roboto_slab_bold)
            paint.isSubpixelText = true
            paint.typeface = robotoFont
            paint.style = Paint.Style.FILL
            paint.color = ContextCompat.getColor(context, R.color.item_comment)
            paint.textSize = 46f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("$days days", 100f, 60f, paint)
            canvas.drawText("$hours hrs", 100f, 110f, paint)
            canvas.drawText("$minutes m", 100f, 160f, paint)
            val stroke = Paint()
            stroke.style = Paint.Style.STROKE
            stroke.strokeWidth = 2f
            stroke.color = Color.BLACK
            stroke.textSize = 46f
            stroke.typeface = robotoFont
            stroke.isAntiAlias = true
            stroke.isSubpixelText = true
            stroke.textAlign = Paint.Align.CENTER
            canvas.drawText("$days days", 100f, 60f, stroke)
            canvas.drawText("$hours hrs", 100f, 110f, stroke)
            canvas.drawText("$minutes m", 100f, 160f, stroke)
            return bitmap
        }

        //Function that returns a bitmap of an arc based on how much time till release.
        fun drawArc(context: Context, arcDegree: Float) : Bitmap{
            val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val rectF = RectF(5f,5f,195f,195f)
            val rectBorder = RectF(-120f,-120f,320f,320f)
            paint.isAntiAlias = true
            paint.color = ContextCompat.getColor(context, R.color.cooldownShade)
            canvas.clipRect(rectF)
            canvas.drawArc(rectBorder, 270f, -360 + arcDegree, true, paint)
            return bitmap
        }

        //Function that returns a bitmap for the "READY" text.
        fun drawTextString(context: Context, text: String) : Bitmap{
            val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val robotoFont = ResourcesCompat.getFont(context, R.font.roboto_slab_bold)
            paint.isSubpixelText = true
            paint.typeface = robotoFont
            paint.style = Paint.Style.FILL
            paint.color = ContextCompat.getColor(context, R.color.item_comment)
            paint.textSize = 32f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(text, 100f, 100f, paint)
            val stroke = Paint()
            stroke.style = Paint.Style.STROKE
            stroke.strokeWidth = 2f
            stroke.color = Color.BLACK
            stroke.textSize = 32f
            stroke.typeface = robotoFont
            stroke.isAntiAlias = true
            stroke.isSubpixelText = true
            stroke.textAlign = Paint.Align.CENTER
            canvas.drawText(text, 100f, 100f, stroke)
            return bitmap
        }
    }
}
