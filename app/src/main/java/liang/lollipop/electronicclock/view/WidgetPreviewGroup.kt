package liang.lollipop.electronicclock.view

import android.content.Context
import android.util.AttributeSet
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

    var spanX = 1
        set(value) {
            field = value
            requestLayout()
        }

    var spanY = 1
        set(value) {
            field = value
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
        super.onLayout(changed, left, top, right, bottom)
        val widthSize = right - left - paddingLeft - paddingRight
        val heightSize = bottom - top - paddingTop - paddingBottom
        val childSize = getChildSize(widthSize, heightSize)
        val childLeft = (widthSize - childSize[0]) / 2 + paddingLeft
        val childTop = (heightSize - childSize[1]) / 2 + paddingTop
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility != View.VISIBLE) {
                continue
            }
            view.layout(childLeft, childTop, childLeft + childSize[0], childTop + childSize[1])
        }
    }

}