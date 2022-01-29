package liang.lollipop.widget.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout
import liang.lollipop.widget.utils.dp

open class WidgetGroup(
    context: Context,
    attr: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : FrameLayout(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attr,
        defStyleAttr,
        0
    )

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    /**
     * 面板组件的集合
     * 用于保存面板对象，从中获取数据
     */
    protected val panelList = ArrayList<Panel<*>>()

    protected var listener: PanelStatusChangedListener? = null
        private set

    protected var isActive = false

    protected var requestToLayout = false

    /**
     * 拖拽的边框宽度，用于调整触发灵敏度
     */
    var dragStrokeWidth = resources.dp(5F)

    var isInEngineeringMode = false

    /**
     * 是否是方形格子
     */
    var isSquareGrid = true

    /**
     * 被选中的卡片
     */
    var selectedPanel: Panel<*>? = null
        set(value) {
            field = value
            pendingTouchRequest = true
            invalidate()
        }

    /**
     * 请求一次手势拦截
     */
    protected var pendingTouchRequest = false

    protected fun Panel<*>.updatePanelLifecycle(active: Boolean) {
        isActive = active
        if (active) {
            onAttachedToWindow()
        } else {
            onDetachedFromWindow()
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isActive = true
        panelList.forEach {
            it.updatePanelLifecycle(true)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isActive = false
        panelList.forEach {
            it.updatePanelLifecycle(false)
        }
    }

    fun notifyInfoChange() {
        panelList.forEach {
            it.onInfoChange()
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        requestToLayout = true
    }

    fun setPanelStatusChangedListener(listener: PanelStatusChangedListener) {
        this.listener = listener
    }

    interface PanelStatusChangedListener {
        /**
         * 用来绘制选中面板边框效果的回调函数
         */
        fun onDrawSelectedPanel(panel: Panel<*>, mode: AbsolutelyWidgetGroup.DragMode, canvas: Canvas)

        /**
         * 子View的点击事件
         */
        fun onChildClick(panel: Panel<*>): Boolean

        /**
         * 取消拖拽的监听器
         */
        fun onCancelDrag(panel: Panel<*>?)

        /**
         * 拖拽结束后的监听器
         */
        fun onDragEnd(panel: Panel<*>?)

        /**
         * 当面板被添加时的监听器
         */
        fun onPanelAdded(panel: Panel<*>)

        /**
         * 当被选中的面板改变时触发的监听器
         */
        fun onSelectedPanelChange(panel: Panel<*>)
    }

    enum class DragMode(val value: Int) {
        None(-1),
        Move(0),
        Left(1),
        Top(2),
        Right(3),
        Bottom(4)
    }

}