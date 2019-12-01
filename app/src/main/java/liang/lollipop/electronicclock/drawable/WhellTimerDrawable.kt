package liang.lollipop.electronicclock.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import kotlin.math.max

/**
 * @author lollipop
 * @date 2019-12-02 00:08
 * 滚轮时间的绘制器
 */
class WhellTimerDrawable: Drawable() {
    override fun draw(canvas: Canvas) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAlpha(alpha: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpacity(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                tmpStringBuilder.append(array[index])
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

}