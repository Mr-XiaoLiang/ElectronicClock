package liang.lollipop.electronicclock.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import kotlin.math.min

/**
 * 为小部件预览的容器
 * @author Lollipop
 * @date 2019-08-22
 *
 * 它的作用只有一个，
 * 使child按照设定的比例在Group中居中显示，
 * 并且保证尺寸是最大的
 */
class WidgetPreviewGroup(context: Context, attr: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    FrameLayout(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int): this(context, attr, defStyleAttr, 0)
    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

    private var spanX = 1
    private var spanY = 1

    private var paddingSize = IntArray(4)

    fun setPadding(vararg sizeArray: Float) {
        when {
            sizeArray.isEmpty() -> {
                changePadding(0, 0, 0, 0)
            }
            sizeArray.size < 2 -> {
                val padding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeArray[0],
                    context.resources.displayMetrics).toInt()
                changePadding(padding, padding, padding, padding)
            }
            sizeArray.size < 4 -> {
                val paddingH = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeArray[0],
                    context.resources.displayMetrics).toInt()
                val paddingV = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeArray[1],
                    context.resources.displayMetrics).toInt()
                changePadding(paddingH, paddingV, paddingH, paddingV)
            }
            else -> {
                val left = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeArray[0],
                    context.resources.displayMetrics).toInt()
                val top = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeArray[1],
                    context.resources.displayMetrics).toInt()
                val right = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeArray[2],
                    context.resources.displayMetrics).toInt()
                val bottom = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeArray[3],
                    context.resources.displayMetrics).toInt()
                changePadding(left, top, right, bottom)
            }
        }

    }

    private fun changePadding(left: Int, top: Int, right: Int, bottom: Int) {
        paddingSize[0] = left
        paddingSize[1] = top
        paddingSize[2] = right
        paddingSize[3] = bottom
        requestLayout()
    }

    fun changeSize(x: Int, y: Int) {
        spanX = x
        spanY = y
        requestLayout()
    }

    fun changeX(x: Int) {
        spanX = x
        requestLayout()
    }

    fun changeY(y: Int) {
        spanY = y
        requestLayout()
    }

    private fun getChildSize(width: Int, height: Int): IntArray {
        val gridSize = min(width / spanX, height / spanY)
        return intArrayOf(spanX * gridSize, spanY * gridSize)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val heightSize = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        val childSize = getChildSize(widthSize, heightSize)
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility != View.VISIBLE) {
                continue
            }
            measureChild(view, childSize[0], childSize[1])
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val widthSize = right - left - paddingSize[0] - paddingSize[2]
        val heightSize = bottom - top - paddingSize[1] - paddingSize[3]
        val childSize = getChildSize(widthSize, heightSize)
        val childLeft = (widthSize - childSize[0]) / 2 + paddingSize[0]
        val childTop = (heightSize - childSize[1]) / 2 + paddingSize[1]
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility != View.VISIBLE) {
                continue
            }
            view.layout(childLeft, childTop, childLeft + childSize[0], childTop + childSize[1])
        }
    }

}