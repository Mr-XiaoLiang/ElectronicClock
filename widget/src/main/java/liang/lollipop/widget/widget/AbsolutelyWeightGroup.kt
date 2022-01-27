package liang.lollipop.widget.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * 绝对位置小部件管理器
 */
class AbsolutelyWeightGroup(context: Context, attr: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    FrameLayout(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int): this(context, attr, defStyleAttr, 0)
    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)



}