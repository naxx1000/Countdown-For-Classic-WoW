package com.rakiwow.classiccountdown

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CooldownView @JvmOverloads constructor(
    context: Context,
    _left: Float,
    _right: Float,
    _top: Float,
    _bottom: Float,
    var _arc: Float,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dpToPxValue: Float = Resources.getSystem().displayMetrics.scaledDensity
    private val paint: Paint = Paint()
    private val rectF1: RectF = RectF(
        _left + dpToPxValue * 6, _top + dpToPxValue * 6, _right - dpToPxValue * 6,
        _bottom - dpToPxValue * 6
    )
    
    private val rectF2: RectF = RectF(_left - 400f, _top - 400f, _right + 400f, _bottom + 400f)

    override fun onDraw(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.cooldownShade)

        canvas.clipRect(rectF1)
        canvas.drawArc(rectF2, 270f, -360 + _arc, true, paint)

        super.onDraw(canvas)
    }
}