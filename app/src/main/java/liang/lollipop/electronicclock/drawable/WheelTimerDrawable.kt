package liang.lollipop.electronicclock.drawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Log
import java.util.*
import kotlin.math.abs


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

        private val TYPE_ARRAY = arrayOf(MONTH, DAY, WEEK, HOUR, MINUTE, SECOND)

        private const val TYPED_A = 1
        private const val TYPED_B = -1

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

    private val numberIndex = IntArray(TYPE_ARRAY.size)
    private val fontSizeArray = FloatArray(TYPE_ARRAY.size)
    private val sectorThickness = FloatArray(TYPE_ARRAY.size)

    val paddings = FloatArray(4)

    var radiusSize = 0.1F

    var arcWeight = 2F

    var typeChangeKey = 10
        set(value) {
            field = if (value < 2) {
                2
            } else {
                value % 60
            }
        }

    private var isTypedChanged = false

    private var fontOffsetY = 0F

    /**
     * 仿真模式
     */
    var simulation = false

    /**
     * 显示格子
     */
    var showGrid = false

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    private val bgPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.BLACK
        alpha = (0.2F * 255).toInt()
        style = Paint.Style.STROKE
    }

    var color: Int
        set(value) {
            paint.color = value
            invalidateSelf()
        }
        get() {
            return paint.color
        }

    var backgroundColor: Int
        set(value) {
            bgPaint.color = value
        }
        get() {
            return bgPaint.color
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
        if (isAnimationEnable) {
            val alpha = paint.alpha
            paint.alpha = abs(alpha * getAngleOffset(SECOND)).toInt().range(0, 255)
//        bgPaint.strokeWidth = 1F
            canvas.drawLine(bounds.left.toFloat(), circleCenter.y,
                bounds.right.toFloat(), circleCenter.y, paint)
            paint.alpha = alpha
        }
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
            monthPosition = calendar.get(Calendar.MONTH) + 1
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
        drawValue(canvas, MONTH, 12, monthPosition, valueProvider.monthArray(isTypedA))
    }

    private fun drawDay(canvas: Canvas) {
        drawValue(canvas, DAY, monthDayCount, monthDayPosition, valueProvider.dayArray(isTypedA))
    }

    private fun drawWeek(canvas: Canvas) {
        drawValue(canvas, WEEK, 7, weekPosition, valueProvider.weekArray(isTypedA))
    }

    private fun drawHour(canvas: Canvas) {
        drawValue(canvas, HOUR, 24, hourPosition, valueProvider.hourArray(isTypedA))
    }

    private fun drawMinute(canvas: Canvas) {
        val position = (System.currentTimeMillis() % ONE_HOUR / ONE_MINUTE).toInt()
        drawValue(canvas, MINUTE, 60, position, valueProvider.minuteArray(isTypedA))
    }

    private fun drawSecond(canvas: Canvas) {
        val position = (System.currentTimeMillis() % ONE_MINUTE / ONE_SECOND).toInt()
        drawValue(canvas, SECOND, 60, position, valueProvider.secondArray(isTypedA))
    }

    private fun drawValue(canvas: Canvas, type: Int, itemCount: Int,
                          position: Int, array: ValueArray) {
        val index = numberIndex[type]
        if (index < 0) {
            return
        }
        val fontSize = fontSizeArray[type]
        val stepAngle = 360F / (itemCount * arcWeight) * getAngleWeight(type)
        val offsetAngle = stepAngle * getAngleOffset(type)
        val count = (90 / stepAngle + 1).toInt()
        val width = sectorThickness[type]
        drawText(canvas, array[position], index.toFloat(),
            offsetAngle, fontSize, width, stepAngle, position)
        for (i in 1..count) {
            val off = i * stepAngle
            drawText(canvas, array[(position + i) % itemCount],
                index.toFloat(), offsetAngle - off, fontSize, width, stepAngle, position + i)
            drawText(canvas, array[(position - i) % itemCount],
                index.toFloat(), offsetAngle + off, fontSize, width, stepAngle, position - i)
        }
    }

    private fun getAngleWeight(type: Int): Float {
        return when (type) {
            MONTH -> {
                2F
            }
            DAY -> {
                1.6F
            }
            WEEK -> {
                1F
            }
            HOUR -> {
                1F
            }
            MINUTE -> {
                1.6F
            }
            SECOND -> {
                1.5F
            }
            else -> {
                1F
            }
        }
    }

    private fun drawText(canvas: Canvas, value: String,
                         index: Float, angle: Float, fontSize: Float,
                         width: Float, radian: Float, position: Int) {
        val k = if (isTypedA) { 0 } else { 1 }
        val drawBg = (simulation || showGrid) && (abs(position % 2) == k)
        canvas.save()
        canvas.translate(circleCenter.x, circleCenter.y)
        canvas.rotate(angle)
        if (drawBg) {
            val radius = index + (width / 2)
            bgPaint.strokeWidth = width
            canvas.drawArc(-radius, -radius, radius, radius,
                radian * -0.5F, radian, false, bgPaint)
        }
        paint.textSize = fontSize
        canvas.drawText(value, index + (width / 2), fontOffsetY, paint)
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
                (day * 1F / allDay) - 0.5F
            } else {
                0F
            }
            DAY, WEEK -> if (simulation) {
                val newTime = calendar.timeZone.getOffset(now) + now
                (newTime % ONE_DAY / ONE_HOUR / 24F) - 0.5F
            } else {
                0F
            }
            HOUR -> if (simulation) {
                (now % ONE_HOUR / ONE_MINUTE / 60F) - 0.5F
            } else {
                0F
            }
            MINUTE -> if (simulation) {
                (now % ONE_MINUTE / ONE_SECOND / 60F) - 0.5F
            } else {
                0F
            }
            SECOND -> {
                (now % ONE_SECOND / 1000F) - 0.5F
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
        val valueLength = IntArray(6)
        valueLength[MONTH] = valueProvider.monthMaxLength(isTypedA)
        valueLength[DAY] = valueProvider.dayMaxLength(isTypedA)
        valueLength[WEEK] = valueProvider.weekMaxLength(isTypedA)
        valueLength[HOUR] = valueProvider.hourMaxLength(isTypedA)
        valueLength[MINUTE] = valueProvider.minuteMaxLength(isTypedA)
        valueLength[SECOND] = valueProvider.secondMaxLength(isTypedA)

        var allLength = 5
        valueLength.forEach {
            allLength += it
        }
        val width = bounds.width() - bounds.width() * (paddings[0] + paddings[2] + radiusSize)
        val step = width / allLength * 0.9F

        var lastIndex = (bounds.width() * (paddings[0] + radiusSize)).toInt()
        TYPE_ARRAY.forEach {
            lastIndex = putIndex(it, step, valueLength[it], lastIndex)
            sectorThickness[it] = step * (valueLength[it] + 1)
        }

        fontSizeArray[MONTH] = refitText(step * valueLength[MONTH],
            valueProvider.monthArray(isTypedA).maxLengthValue)
        fontSizeArray[DAY] = refitText(step * valueLength[DAY],
            valueProvider.dayArray(isTypedA).maxLengthValue)
        fontSizeArray[WEEK] = refitText(step * valueLength[WEEK],
            valueProvider.weekArray(isTypedA).maxLengthValue)
        fontSizeArray[HOUR] = refitText(step * valueLength[HOUR],
            valueProvider.hourArray(isTypedA).maxLengthValue)
        fontSizeArray[MINUTE] = refitText(step * valueLength[MINUTE],
            valueProvider.minuteArray(isTypedA).maxLengthValue)
        fontSizeArray[SECOND] = refitText(step * valueLength[SECOND],
            valueProvider.secondArray(isTypedA).maxLengthValue)

        val centerY = (bounds.height() - paddings[1] - paddings[3]) / 2 + bounds.top + paddings[1]
        circleCenter.set(0F, centerY)

        val fm = paint.fontMetrics
        fontOffsetY = (fm.descent - fm.ascent) / 2 - fm.descent

        if (!isRunning) {
            invalidateSelf()
        }
    }

    private fun refitText(targetWidth: Float, text: String): Float {
        if (text.isEmpty()) {
            return 1F
        }
        val threshold = 0.5f
        val textPaint = paint
        var preferredTextSize = targetWidth
        var minTextSize = 1F
        while (preferredTextSize - minTextSize > threshold) {
            val size = (preferredTextSize + minTextSize) / 2
            textPaint.textSize = size
            if (textPaint.measureText(text) >= targetWidth) {
                // too big
                preferredTextSize = size
            } else {
                // too small
                minTextSize = size
            }
        }
        return minTextSize
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
            return monthArray(isA).maxLength
        }

        fun dayMaxLength(isA: Boolean): Int {
            return dayArray(isA).maxLength
        }

        fun weekMaxLength(isA: Boolean): Int {
            return weekArray(isA).maxLength
        }

        fun hourMaxLength(isA: Boolean): Int {
            return hourArray(isA).maxLength
        }

        fun minuteMaxLength(isA: Boolean): Int {
            return minuteArray(isA).maxLength
        }

        fun secondMaxLength(isA: Boolean): Int {
            return secondArray(isA).maxLength
        }

        fun monthArray(isA: Boolean): ValueArray {
            return getArrayByType(isA, monthValueA, monthValueB)
        }

        fun dayArray(isA: Boolean): ValueArray {
            return getArrayByType(isA, dayValueA, dayValueB)
        }

        fun weekArray(isA: Boolean): ValueArray {
            return getArrayByType(isA, weekValueA, weekValueB)
        }

        fun hourArray(isA: Boolean): ValueArray {
            return getArrayByType(isA, hourValueA, hourValueB)
        }

        fun minuteArray(isA: Boolean): ValueArray {
            return getArrayByType(isA, minuteValueA, minuteValueB)
        }

        fun secondArray(isA: Boolean): ValueArray {
            return getArrayByType(isA, secondValueA, secondValueB)
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

        var maxLengthValue = ""

        val indices = IntRange(0, size - 1)

        operator fun get(index: Int): String {
            return array[(index + size) % size]
        }

        operator fun set(index: Int, value: String) {
            if (index >= size || index < 0) {
                return
            }
            array[index] = value
            if (value.length > maxLength) {
                maxLength = value.length
                maxLengthValue = value
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

    private fun Int.range(min: Int, max: Int): Int {
        return when {
            this < min -> min
            this > max -> max
            else -> this
        }
    }

}