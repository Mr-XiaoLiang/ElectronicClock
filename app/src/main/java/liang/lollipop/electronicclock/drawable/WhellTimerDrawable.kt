package liang.lollipop.electronicclock.drawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import java.util.*


/**
 * @author lollipop
 * @date 2019-12-02 00:08
 * 滚轮时间的绘制器
 */
class WheelTimerDrawable(private val valueProvider: ValueProvider): Drawable(), Animatable,
    ValueAnimator.AnimatorUpdateListener {

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

    var radiusSize = 0.1F

    var arcWeight = 2F

    var typeChangeKey = 10
        set(value) {
            field = value % 60
        }

    private var isTypedChanged = false

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

    private var dayTimeMillis = 0L

    private var monthDayCount = 0

    private var monthDayPosition = 0

    private var monthPosition = 0

    private var weekPosition = 0

    private var hourPosition = 0

    private var hourTimeMillis = 0L

    private var isAnimationEnable = false

    private var circleCenter = PointF(0F, 0F)

    private val calendar: Calendar by lazy {
        Calendar.getInstance()
    }

    private val animator: ValueAnimator by lazy {
        ValueAnimator().apply {
            addUpdateListener(this@WheelTimerDrawable)
        }
    }

    override fun draw(canvas: Canvas) {
        checkType()
        checkDayTime()
        checkHourTime()
        drawMonth(canvas)
        drawDay(canvas)
        drawWeek(canvas)
        drawHour(canvas)
        drawMinute(canvas)
        drawSecond(canvas)
        canvas.drawLine(0F, circleCenter.y, bounds.right.toFloat(), circleCenter.y, paint)
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

    private fun checkType() {
        // 每隔一段时间，更新一次类型，更新类型的同时，更新排版
        val isChangeType = (System.currentTimeMillis() % ONE_HOUR / ONE_MINUTE) % typeChangeKey == 0L
        if (!isTypedChanged && isChangeType) {
            typeValue = if (isTypedA) { TYPED_B } else { TYPED_A }
            isTypedChanged = true
            updateLocation()
        } else if (!isChangeType) {
            isTypedChanged = false
        }
    }

    private fun checkDayTime() {
        val now = System.currentTimeMillis()
        if (now / ONE_DAY != dayTimeMillis) {
            dayTimeMillis = now / ONE_DAY
            calendar.timeInMillis = now
            monthDayPosition = calendar.get(Calendar.DAY_OF_MONTH) - 1
            monthPosition = calendar.get(Calendar.MONTH)
            weekPosition = calendar.get(Calendar.DAY_OF_WEEK) - 1
            monthDayCount = getDayCountByMonth(now)

        }
    }

    private fun checkHourTime() {
        val now = System.currentTimeMillis()
        if (now / ONE_HOUR != hourTimeMillis) {
            hourTimeMillis = now / ONE_HOUR
            calendar.timeInMillis = System.currentTimeMillis()
            hourPosition = calendar.get(Calendar.HOUR_OF_DAY)
        }
    }

    private fun drawMonth(canvas: Canvas) {
        drawValue(canvas, MONTH, 12, monthPosition, valueProvider.monthValueA, valueProvider.monthValueB)
    }

    private fun drawDay(canvas: Canvas) {
        drawValue(canvas, DAY, monthDayCount, monthDayPosition, valueProvider.dayValueA, valueProvider.dayValueB)
    }

    private fun drawWeek(canvas: Canvas) {
        drawValue(canvas, WEEK, 7, weekPosition, valueProvider.weekValueA, valueProvider.weekValueB)
    }

    private fun drawHour(canvas: Canvas) {
        drawValue(canvas, HOUR, 24, hourPosition, valueProvider.hourValueA, valueProvider.hourValueB)
    }

    private fun drawMinute(canvas: Canvas) {
        val position = (System.currentTimeMillis() % ONE_HOUR / ONE_MINUTE).toInt()
        drawValue(canvas, MINUTE, 60, position, valueProvider.minuteValueA, valueProvider.minuteValueB)
    }

    private fun drawSecond(canvas: Canvas) {
        val position = (System.currentTimeMillis() % ONE_MINUTE / ONE_SECOND).toInt()
        drawValue(canvas, SECOND, 60, position, valueProvider.secondValueA, valueProvider.secondValueB)
    }

    private fun drawValue(canvas: Canvas, type: Int, itemCount: Int,
                          position: Int, arrayA: ValueArray, arrayB: ValueArray) {
        val index = numberIndex[type]
        if (index < 0) {
            return
        }
        val stepAngle = 360F / (itemCount * arcWeight) * getAngleWeight(type)
        val offsetAngle = stepAngle * (getAngleOffset(type))
        val count = (90 / stepAngle + 1).toInt()
        drawText(canvas, getValue(position, arrayA, arrayB), index.toFloat(), offsetAngle)
        for (i in 1..count) {
            val off = i * stepAngle
            drawText(canvas, getValue((position + i) % itemCount, arrayA, arrayB),
                index.toFloat(), offsetAngle - off)
            drawText(canvas, getValue((position - i) % itemCount, arrayA, arrayB),
                index.toFloat(), offsetAngle + off)
        }
    }

    private fun getAngleWeight(type: Int): Float {
        return when (type) {
            MONTH -> {
                2F
            }
            DAY -> {
                1.5F
            }
            WEEK -> {
                1F
            }
            HOUR -> {
                1F
            }
            MINUTE -> {
                1.2F
            }
            SECOND -> {
                1.1F
            }
            else -> {
                1F
            }
        }
    }

    private fun drawText(canvas: Canvas, value: String, index: Float, angle: Float) {
        canvas.save()
        canvas.translate(circleCenter.x, circleCenter.y)
        canvas.rotate(angle)
        canvas.drawText(value, index, fontOffsetY, paint)
        canvas.restore()
    }

    private fun getDayCountByMonth(time: Long): Int {
        calendar.timeInMillis = time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun getAngleOffset(type: Int): Float {
        if (!isAnimationEnable) {
            return 0F
        }
        val now = System.currentTimeMillis()
        calendar.timeInMillis = now
        return when(type) {
            MONTH -> if (simulation) {
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val allDay = getDayCountByMonth(now)
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
        val monthLength = valueProvider.monthMaxLength(isTypedA)
        val dayLength = valueProvider.dayMaxLength(isTypedA)
        val weekLength = valueProvider.weekMaxLength(isTypedA)
        val hourLength = valueProvider.hourMaxLength(isTypedA)
        val minuteLength = valueProvider.minuteMaxLength(isTypedA)
        val secondLength = valueProvider.secondMaxLength(isTypedA)

        val allLength = monthLength + dayLength + weekLength + hourLength + minuteLength + secondLength + 5
        val width = bounds.width() - bounds.width() * (paddings[0] + paddings[2] + radiusSize)
        val step = width / allLength * 0.9F

        paint.textSize = step

        var lastIndex = (bounds.width() * (paddings[0] + radiusSize)).toInt()
        lastIndex = putIndex(MONTH, step, monthLength, lastIndex)
        lastIndex = putIndex(DAY, step, dayLength, lastIndex)
        lastIndex = putIndex(WEEK, step, weekLength, lastIndex)
        lastIndex = putIndex(HOUR, step, hourLength, lastIndex)
        lastIndex = putIndex(MINUTE, step, minuteLength, lastIndex)
        putIndex(SECOND, step, secondLength, lastIndex)

        val centerY = (bounds.height() - paddings[1] - paddings[3]) / 2 + bounds.top + paddings[1]
        circleCenter.set(0F, centerY)

        val fm = paint.fontMetrics
        fontOffsetY = (fm.descent - fm.ascent) / 2 - fm.descent

        if (!isRunning) {
            invalidateSelf()
        }
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

        fun copyValueFromRes(context: Context, valueArray: ValueArray, vararg arrayIds: Int) {
            val stringArrays = getStringArrays(context, *arrayIds)
            for (i in valueArray.indices) {
                valueArray[i] = getValueByArray(stringArrays, i, delimiter)
            }
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

        private fun getArrayByType(isA: Boolean,arrayA: ValueArray,
                                   arrayB: ValueArray): ValueArray {
            return if (isA) {
                arrayA
            } else {
                arrayB
            }
        }

        fun monthMaxLength(isA: Boolean): Int {
            return getArrayByType(isA, monthValueA, monthValueB).maxLength
        }

        fun dayMaxLength(isA: Boolean): Int {
            return getArrayByType(isA, dayValueA, dayValueB).maxLength
        }

        fun weekMaxLength(isA: Boolean): Int {
            return getArrayByType(isA, weekValueA, weekValueB).maxLength
        }

        fun hourMaxLength(isA: Boolean): Int {
            return getArrayByType(isA, hourValueA, hourValueB).maxLength
        }

        fun minuteMaxLength(isA: Boolean): Int {
            return getArrayByType(isA, minuteValueA, minuteValueB).maxLength
        }

        fun secondMaxLength(isA: Boolean): Int {
            return getArrayByType(isA, secondValueA, secondValueB).maxLength
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
        return isAnimationEnable && animator.isRunning
    }

    override fun start() {
        isAnimationEnable = true
        animator.setFloatValues(0F, 1F)
        animator.duration = 1000L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()
    }

    override fun stop() {
        isAnimationEnable = false
        animator.cancel()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        invalidateSelf()
    }

}