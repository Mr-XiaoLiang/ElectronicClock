package liang.lollipop.electronicclock.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

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



}