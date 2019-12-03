package liang.lollipop.electronicclock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import liang.lollipop.electronicclock.drawable.WhellTimerDrawable

/**
 * @author lollipop
 * @date 2019-12-03 23:05
 * 滚轮时间的View
 */
class WhellTimerView(context: Context, attr: AttributeSet?,
                     defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val valueProvider = WhellTimerDrawable.ValueProvider()
    private val timerDrawable = WhellTimerDrawable(valueProvider)

    init {
        timerDrawable.callback = this
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        timerDrawable.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        timerDrawable.setBounds(left, top, right, bottom)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == timerDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    fun setMonthAValue(vararg arrayId: Int) {
        setValue(valueProvider.monthValueA, *arrayId)
    }

    fun setMonthBValue(vararg arrayId: Int) {
        setValue(valueProvider.monthValueB, *arrayId)
    }

    fun setDayAValue(vararg arrayId: Int) {
        setValue(valueProvider.dayValueA, *arrayId)
    }

    fun setDayBValue(vararg arrayId: Int) {
        setValue(valueProvider.dayValueB, *arrayId)
    }

    fun setWeekAValue(vararg arrayId: Int) {
        setValue(valueProvider.weekValueA, *arrayId)
    }

    fun setWeekBValue(vararg arrayId: Int) {
        setValue(valueProvider.weekValueB, *arrayId)
    }

    fun setHourAValue(vararg arrayId: Int) {
        setValue(valueProvider.hourValueA, *arrayId)
    }

    fun setHourBValue(vararg arrayId: Int) {
        setValue(valueProvider.hourValueB, *arrayId)
    }

    fun setMinuteAValue(vararg arrayId: Int) {
        setValue(valueProvider.minuteValueA, *arrayId)
    }

    fun setMinuteBValue(vararg arrayId: Int) {
        setValue(valueProvider.minuteValueB, *arrayId)
    }

    fun setSecondAValue(vararg arrayId: Int) {
        setValue(valueProvider.secondValueA, *arrayId)
    }

    fun setSecondBValue(vararg arrayId: Int) {
        setValue(valueProvider.secondValueB, *arrayId)
    }

    fun notifyDatasetChange() {
        timerDrawable.notifyValueChange()
    }

    private fun setValue(valueArray: WhellTimerDrawable.ValueArray, vararg arrayId: Int) {
        valueProvider.copyValueFromRes(context, valueArray, *arrayId)
    }
}