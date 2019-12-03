package liang.lollipop.electronicclock.drawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import liang.lollipop.electronicclock.offScreen.InvalidateCallback
import liang.lollipop.electronicclock.offScreen.Painter
import liang.lollipop.electronicclock.offScreen.PainterHelper
import java.util.*
import kotlin.math.max


/**
 * @author lollipop
 * @date 2019-12-02 00:08
 * 滚轮时间的绘制器
 */
class WhellTimerDrawable(private val valueProvider: ValueProvider): Drawable(),
    Painter, Animatable {

    companion object {
        private const val MONTH = 0
        private const val DAY = 1
        private const val WEEK = 2
        private const val HOUR = 3
        private const val MINUTE = 4
        private const val SECOND = 5

        private const val TYPED_A = 1F
        private const val TYPED_B = -1F

        private const val ONE_SECOND = 1000L
        private const val ONE_MINUTE = ONE_SECOND * 60
        private const val ONE_HOUR = ONE_MINUTE * 60
        private const val ONE_DAY = ONE_HOUR * 24
    }

    private var typeValue = TYPED_A

    private val isTypedA: Boolean
        get() {
            return typeValue >= 0
        }

    private val numberIndex = IntArray(6)

    val paddings = FloatArray(4)

    var radiusSize = 3

    var arcWeight = 2F

    private var fontOffsetY = 0F

    /**
     * 仿真模式
     */
    var simulation = false

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.BLACK
        textAlign = Paint.Align.LEFT
    }

    var color: Int
        set(value) {
            paint.color = value
        }
        get() {
            return paint.color
        }

    private var circleCenter = PointF(0F, 0F)

    private val painterHelper = PainterHelper(this)

    private val calendar: Calendar by lazy {
        Calendar.getInstance()
    }

    private val animator: ValueAnimator by lazy {
        ValueAnimator()
    }

    override fun setInvalidateCallback(callback: InvalidateCallback) {
        painterHelper.setInvalidateCallback(callback)
    }

    override fun onSizeChange(width: Int, height: Int) {
        painterHelper.onSizeChange(width, height)
    }

    override fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        painterHelper.onInsetChange(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        drawMonth(canvas)
        drawDay(canvas)
        drawWeek(canvas)
        drawHour(canvas)
        drawMinute(canvas)
        drawSecond(canvas)
    }

    override fun onShow() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onHide() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    private fun drawMonth(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()
        val position = calendar.get(Calendar.MONTH)
        drawValue(canvas, MONTH, 12, position, valueProvider.monthValueA, valueProvider.monthValueB)
    }

    private fun drawDay(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()
        val position = calendar.get(Calendar.DAY_OF_MONTH) - 1
        drawValue(canvas, DAY, 31, position, valueProvider.dayValueA, valueProvider.dayValueB)
    }

    private fun drawWeek(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()
        val position = calendar.get(Calendar.DAY_OF_WEEK) - 1
        drawValue(canvas, WEEK, 7, position, valueProvider.weekValueA, valueProvider.weekValueB)
    }

    private fun drawHour(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()
        val position = calendar.get(Calendar.HOUR_OF_DAY)
        drawValue(canvas, HOUR, 24, position, valueProvider.hourValueA, valueProvider.hourValueB)
    }

    private fun drawMinute(canvas: Canvas) {
        val position = (System.currentTimeMillis() % ONE_HOUR / ONE_MINUTE).toInt()
        drawValue(canvas, MINUTE, 60, position, valueProvider.minuteValueA, valueProvider.minuteValueB)
    }

    private fun drawSecond(canvas: Canvas) {
        val position = (System.currentTimeMillis() % ONE_MINUTE / ONE_SECOND).toInt()
        drawValue(canvas, SECOND, 60, position, valueProvider.secondValueA, valueProvider.secondValueB)
    }

    private fun drawValue(canvas: Canvas, type: Int, itemCount: Int, position: Int, arrayA: ValueArray, arrayB: ValueArray) {
        val index = numberIndex[type]
        if (index < 0) {
            return
        }
        val stepAngle = 360F / (itemCount * arcWeight)
        val offsetAngle = stepAngle * getAngleOffset(type)
        val count = (90 / stepAngle + 1).toInt()
        drawText(canvas, getValue(position, arrayA, arrayB), index.toFloat(), offsetAngle)
        for (i in 1 until count) {
            val off = i * stepAngle
            drawText(canvas, getValue(position + i, arrayA, arrayB), index.toFloat(), offsetAngle + off)
            drawText(canvas, getValue(position - i, arrayA, arrayB), index.toFloat(), offsetAngle - off)
        }
    }

    private fun drawText(canvas: Canvas, value: String, index: Float, angle: Float) {
        canvas.save()
        canvas.rotate(angle)
        canvas.translate(circleCenter.x, circleCenter.y)
        canvas.drawText(value, index, fontOffsetY, paint)
        canvas.restore()
    }

    private fun getAngleOffset(type: Int): Float {
        val now = System.currentTimeMillis()
        calendar.timeInMillis = now
        return when(type) {
            MONTH -> if (simulation) {
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val allDay = calendar.get(Calendar.DAY_OF_MONTH)
                day * 1F / allDay
            } else {
                0F
            }
            DAY, WEEK -> if (simulation) {
                val newTime = calendar.timeZone.getOffset(now) + now
                newTime % ONE_DAY / ONE_HOUR / 24F
            } else {
                0F
            }
            HOUR -> if (simulation) {
                now % ONE_HOUR / ONE_MINUTE / 60F
            } else {
                0F
            }
            MINUTE -> if (simulation) {
                now % ONE_MINUTE / ONE_SECOND / 60F
            } else {
                0F
            }
            SECOND -> {
                now % ONE_SECOND / 1000F
            }
            else -> 0F
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        updateLocation()
    }

    fun notifyValueChange() {
        updateLocation()
    }

    private fun updateLocation() {
        if (bounds.isEmpty) {
            return
        }
        val monthLength = valueProvider.monthMaxLength
        val dayLength = valueProvider.dayMaxLength
        val weekLength = valueProvider.weekMaxLength
        val hourLength = valueProvider.hourMaxLength
        val minuteLength = valueProvider.minuteMaxLength
        val secondLength = valueProvider.secondMaxLength

        val allLength = monthLength + dayLength + weekLength + hourLength + minuteLength + secondLength + 5
        val width = bounds.width() * (1 - paddings[0] - paddings[2])
        val step = width / allLength

        paint.textSize = step

        var lastIndex = radiusSize
        lastIndex = putIndex(MONTH, step, monthLength, lastIndex)
        lastIndex = putIndex(DAY, step, dayLength, lastIndex)
        lastIndex = putIndex(WEEK, step, weekLength, lastIndex)
        lastIndex = putIndex(HOUR, step, hourLength, lastIndex)
        lastIndex = putIndex(MINUTE, step, minuteLength, lastIndex)
        putIndex(SECOND, step, secondLength, lastIndex)

        val centerY = (bounds.height() - paddings[1] - paddings[3]) / 2 + bounds.top + paddings[1]
        circleCenter.set(paddings[0], centerY)

        val fm = paint.fontMetrics
        fontOffsetY = (fm.descent - fm.ascent) / 2 - fm.descent

        painterHelper.callInvalidate()
    }

    private fun putIndex(type: Int, step: Float, length: Int, last: Int): Int {
        if (length <= 0) {
            numberIndex[type] = -1
            return last
        }
        if (last > 0) {
            numberIndex[type] = (last + step).toInt()
        } else {
            numberIndex[type] = 0
        }
        return (numberIndex[type] + length * step).toInt()
    }

    private fun getValue(index: Int, arrayA: ValueArray, arrayB: ValueArray): String {
        return if (isTypedA) {
            arrayA[(index + arrayA.size) % arrayA.size]
        } else {
            arrayB[(index + arrayB.size) % arrayB.size]
        }
    }

    class ValueProvider {

        private val tmpStringBuilder: StringBuilder by lazy {
            StringBuilder()
        }

        val monthValueA = ValueArray(12)
        val monthValueB = ValueArray(12)

        val dayValueA = ValueArray(31)
        val dayValueB = ValueArray(31)

        val weekValueA = ValueArray(7)
        val weekValueB = ValueArray(7)

        val hourValueA = ValueArray(24)
        val hourValueB = ValueArray(24)

        val minuteValueA = ValueArray(60)
        val minuteValueB = ValueArray(60)

        val secondValueA = ValueArray(60)
        val secondValueB = ValueArray(60)

        var delimiter = " "

        val monthMaxLength: Int
            get() {
                return max(monthValueA.maxLength, monthValueB.maxLength)
            }

        val dayMaxLength: Int
            get() {
                return max(dayValueA.maxLength, dayValueB.maxLength)
            }

        val weekMaxLength: Int
            get() {
                return max(weekValueA.maxLength, weekValueB.maxLength)
            }

        val hourMaxLength: Int
            get() {
                return max(hourValueA.maxLength, hourValueB.maxLength)
            }

        val minuteMaxLength: Int
            get() {
                return max(minuteValueA.maxLength, minuteValueB.maxLength)
            }

        val secondMaxLength: Int
            get() {
                return max(secondValueA.maxLength, secondValueB.maxLength)
            }

        fun copyValueFromRes(context: Context, valueArray: ValueArray, vararg arrayIds: Int) {
            val stringArrays = getStringArrays(context, *arrayIds)
            for (i in valueArray.indices) {
                valueArray[i] = getValueByArray(stringArrays, i, delimiter)
            }
        }

        private fun getValueByArray(values: Array<ValueArray>, index: Int, delimiter: String): String {
            tmpStringBuilder.setLength(0)
            for (arrayIndex in values.indices) {
                val array = values[arrayIndex]
                if (arrayIndex != 0) {
                    tmpStringBuilder.append(delimiter)
                }
                tmpStringBuilder.append(array[index % array.size])
            }
            return tmpStringBuilder.toString()
        }

        private fun getStringArrays(context: Context, vararg arrayIds: Int): Array<ValueArray> {
            val res = context.resources
            return Array(arrayIds.size) { index ->
                ValueArray.copy(res.getStringArray(arrayIds[index]))
            }
        }

    }

    class ValueArray(val size: Int, initFun: ((Int) -> String) = { "" }) {

        companion object {
            fun copy(strArray: Array<String>): ValueArray {
                return ValueArray(strArray.size) { strArray[it] }
            }
        }

        private val array = Array(size, initFun)

        var maxLength = 0
            private set

        val indices = IntRange(0, size - 1)

        operator fun get(index: Int): String {
            if (index >= size || index < 0) {
                return ""
            }
            return array[index]
        }

        operator fun set(index: Int, value: String) {
            if (index >= size || index < 0) {
                return
            }
            array[index] = value
            if (value.length > maxLength) {
                maxLength = value.length
            }
        }

        fun clean() {
            for (i in array.indices) {
                array[i] = ""
            }
            maxLength = 0
        }

    }

    override fun isRunning(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}