package liang.lollipop.electronicclock.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @author lollipop
 * @date 2019-07-30 20:43
 * 小部件管理器
 */
class WidgetGroup(context: Context, attr: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    FrameLayout(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int): this(context, attr, defStyleAttr, 0)
    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

}