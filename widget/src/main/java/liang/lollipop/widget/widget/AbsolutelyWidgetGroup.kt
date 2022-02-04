package liang.lollipop.widget.widget

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import liang.lollipop.widget.utils.Utils
import java.util.*

/**
 * 绝对位置小部件管理器
 * 大过年的写什么代码，醉了
 */
class AbsolutelyWidgetGroup(
    context: Context,
    attr: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : WidgetGroup(context, attr, defStyleAttr, defStyleRes) {

    companion object {
        private val logger = Utils.loggerI("AbsolutelyWeightGroup")
        private const val NO_ID = -1
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attr,
        defStyleAttr,
        0
    )

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    /**
     * 自定义绘制及优先级顺序的列表
     * 它主要存在于可以重叠的布局中
     * 因为GridWidgetGroup不允许重叠，所以不需要考虑优先级
     */
    private val priorityList = ArrayList<Panel<*>>()

    /**
     * 用于实例化Panel中View的上下文
     */
    private val layoutInflater = LayoutInflater.from(context)

    /**
     * 回收复用的Rect对象集合
     */
    private val recyclerRectList = LinkedList<Rect>()

    /**
     * 子View的长按点击事件
     */
    private var childLongClickListener: ((panel: Panel<*>) -> Boolean)? = null

    /**
     * 当前活跃的手指的id
     */
    private var activeTouchId = -1

    /**
     * 上次手指的位置
     */
    private var lastTouchLocation = PointF()

    /**
     * 手指按下的时间戳
     */
    private var touchDownTime = 0L

    /**
     * 是否是拖拽模式
     */
    private val isDragState: Boolean
        get() {
            return selectedPanel != null
        }

    /**
     * 拖拽模式
     */
    private var dragMode = DragMode.None

    /**
     * 锁定拖动和尺寸改变
     */
    var lockedBuild = false

    /**
     * 锁定手指事件
     */
    var lockedTouch = false

    /**
     * 添加面板
     * 添加View的唯一途径
     * Group需要根据panel的一些参数决定小部件怎么进行布局排版
     */
    fun addPanel(panel: Panel<*>): Boolean {
        logger("addPanel()")
        if (panel.view == null) {
            panel.create(layoutInflater, this)
        }
        panelList.add(panel)
        addView(panel.view)
        setChildLongClick(panel)
        setChildClick(panel)
        if (isActive) {
            panel.updatePanelLifecycle(true)
        }
        listener?.onPanelAdded(panel)
        notifyPanelPriorityChanged()
        return true
    }

    /**
     * 移除一个面板
     */
    fun removePanel(panel: Panel<*>) {
        // 如果被移除的panel是选中的panel，那么就放弃
        if (panel == selectedPanel) {
            cancelSelected()
        }
        // 如果在集合中成功移除了面板，那么也从View中移除相应的View
        if (panelList.remove(panel)) {
            super.removeView(panel.view)
        }
        priorityList.remove(panel)
    }

    override fun performClick(): Boolean {
        return if (isDragState && dragMode != DragMode.None) {
            false
        } else {
            super.performClick()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    /**
     * 当子View被长按时，触发
     */
    fun onChildLongClick(listener: ((Panel<*>) -> Boolean)?) {
        childLongClickListener = listener
        for (p in panelList) {
            setChildLongClick(p)
        }
    }

    private fun cancelSelected() {
        // TODO
//        GridWidgetGroup.logger("cancelSelected")
//        pushPanelWhenTouch()
//        cancelDragListener?.invoke(selectedPanel)
//        selectedPanel = null
//        activeActionId = GridWidgetGroup.NO_ID
//        invalidate()
    }

    private fun setChildLongClick(panel: Panel<*>) {
        if (panel.customClick) {
            return
        }
        val listener = childLongClickListener
        if (listener != null) {
            panel.view?.setOnLongClickListener { listener(panel) }
        } else {
            panel.view?.setOnLongClickListener(null)
        }
    }

    private fun setChildClick(panel: Panel<*>) {
        if (panel.customClick) {
            return
        }
        panel.view?.setOnClickListener {
            if (listener?.onChildClick(panel) != true) {
                panel.callOnClick(it)
            }
        }
    }

    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        // 如果序号是在重排序的面板集合范围内，那么尝试从其中拿
        if (drawingPosition in priorityList.indices) {
            val panel = priorityList[drawingPosition]
            // 找到真实的序号
            val index = indexOfChild(panel.view)
            if (index >= 0) {
                return index
            }
        }
        return super.getChildDrawingOrder(childCount, drawingPosition)
    }

    /**
     * 通知并发起一次优先级的重排序
     */
    fun notifyPanelPriorityChanged() {
        priorityList.clear()
        priorityList.addAll(panelList)
        priorityList.sortBy { it.priority }
        invalidate()
    }


}