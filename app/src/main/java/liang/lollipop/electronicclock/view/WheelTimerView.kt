package liang.lollipop.electronicclock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import liang.lollipop.electronicclock.drawable.WheelTimerDrawable

/**
 * @author lollipop
 * @date 2019-12-03 23:05
 * 滚轮时间的View
 */
class WheelTimerView(context: Context, attr: AttributeSet?,
                     defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val valueProvider = WheelTimerDrawable.ValueProvider()
    private val timerDrawable = WheelTimerDrawable(valueProvider)

    private var isRun = false

    companion object {
        private val EMPTY_ARRAY = IntArray(0)
    }

    init {
        timerDrawable.callback = this
    }

    private val tempArray: ArrayList<Int> by lazy {
        ArrayList<Int>()
    }

    var simulation: Boolean
        set(value) {
            timerDrawable.simulation = value
        }
        get() {
            return timerDrawable.simulation
        }

    var radiusSize: Float
        set(value) {
            timerDrawable.radiusSize = value
        }
        get() {
            return timerDrawable.radiusSize
        }

    var arcWeight: Float
        set(value) {
            timerDrawable.arcWeight = value
        }
        get() {
            return timerDrawable.arcWeight
        }

    var typeChangeKey: Int
        set(value) {
            timerDrawable.typeChangeKey = value
        }
        get() {
            return timerDrawable.typeChangeKey
        }

    var color: Int
        set(value) {
            timerDrawable.color = value
        }
        get() {
            return timerDrawable.color
        }

    fun setPaddingWeight(left: Float, top: Float, right: Float, bottom: Float) {
        timerDrawable.paddings.let {
            it[0] = left
            it[1] = top
            it[2] = right
            it[3] = bottom
        }
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

    fun start() {
        isRun = true
        checkRunning()
    }

    fun stop() {
        isRun = false
        checkRunning()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        checkRunning()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        checkRunning()
    }

    private fun checkRunning() {
        if (isRun && isAttachedToWindow) {
            timerDrawable.start()
        } else {
            timerDrawable.stop()
        }
    }

    fun setMonthAValue(vararg arrayId: Int) {
        setValue(valueProvider.monthValueA, arrayId)
    }

    fun setMonthBValue(vararg arrayId: Int) {
        setValue(valueProvider.monthValueB, arrayId)
    }

    fun setDayAValue(vararg arrayId: Int) {
        setValue(valueProvider.dayValueA, arrayId)
    }

    fun setDayBValue(vararg arrayId: Int) {
        setValue(valueProvider.dayValueB, arrayId)
    }

    fun setWeekAValue(vararg arrayId: Int) {
        setValue(valueProvider.weekValueA, arrayId)
    }

    fun setWeekBValue(vararg arrayId: Int) {
        setValue(valueProvider.weekValueB, arrayId)
    }

    fun setHourAValue(vararg arrayId: Int) {
        setValue(valueProvider.hourValueA, arrayId)
    }

    fun setHourBValue(vararg arrayId: Int) {
        setValue(valueProvider.hourValueB, arrayId)
    }

    fun setMinuteAValue(vararg arrayId: Int) {
        setValue(valueProvider.minuteValueA, arrayId)
    }

    fun setMinuteBValue(vararg arrayId: Int) {
        setValue(valueProvider.minuteValueB, arrayId)
    }

    fun setSecondAValue(vararg arrayId: Int) {
        setValue(valueProvider.secondValueA, arrayId)
    }

    fun setSecondBValue(vararg arrayId: Int) {
        setValue(valueProvider.secondValueB, arrayId)
    }

    fun notifyDataSetChange() {
        timerDrawable.notifyValueChange()
    }

    private fun setValue(valueArray: WheelTimerDrawable.ValueArray, arrayId: IntArray) {
        if (arrayId.isEmpty()) {
            valueArray.clean()
            return
        }
        val idArray = getEffectiveId(arrayId)
        if (idArray.isEmpty()) {
            valueArray.clean()
            return
        }
        valueProvider.copyValueFromRes(context, valueArray, *idArray)
    }

    private fun getEffectiveId(arrayId: IntArray): IntArray {
        tempArray.clear()
        for (id in arrayId) {
            if (id != 0) {
                tempArray.add(id)
            }
        }
        if (tempArray.isEmpty()) {
            return EMPTY_ARRAY
        }
        return IntArray(tempArray.size) { i -> tempArray[i]}
    }
}