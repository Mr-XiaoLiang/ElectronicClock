package liang.lollipop.widget.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import java.util.*
import kotlin.collections.ArrayList

/**
 * 绝对位置小部件管理器
 */
class AbsolutelyWeightGroup(context: Context, attr: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    FrameLayout(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int): this(context, attr, defStyleAttr, 0)
    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

    /**
     * 面板组件的集合
     * 用于保存面板对象，从中获取数据
     */
    private val panelList = ArrayList<Panel<*>>()

    /**
     * 用于实例化Panel中View的上下文
     */
    private val layoutInflater = LayoutInflater.from(context)

    /**
     * 回收复用的Rect对象集合
     */
    private val recyclerRectList = LinkedList<Rect>()

}