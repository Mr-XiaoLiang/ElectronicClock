package liang.lollipop.widget.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
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
        if (lockedTouch) {
            return true
        }
        if (lockedBuild) {
            return super.onInterceptTouchEvent(ev)
        }
        ev ?: return super.onInterceptTouchEvent(ev)

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                onTouchDown(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                onTouchMove(ev)
            }
            MotionEvent.ACTION_UP -> {
                onTouchUp(ev)
            }
            MotionEvent.ACTION_CANCEL -> {
                onTouchUp(ev)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onTouchPointUp(ev)
            }
        }

        return isDragState || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (lockedTouch) {
            return true
        }
        if (lockedBuild) {
            return super.onTouchEvent(event)
        }
        event ?: return super.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                onTouchDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                onTouchMove(event)
            }
            MotionEvent.ACTION_UP -> {
                onTouchUp(event)
            }
            MotionEvent.ACTION_CANCEL -> {
                onTouchUp(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onTouchPointUp(event)
            }
        }
        return isDragState || super.onTouchEvent(event)
    }

    private fun onTouchDown(event: MotionEvent) {
        // TODO
    }

    private fun onTouchMove(event: MotionEvent) {
        // TODO
    }

    private fun onTouchUp(event: MotionEvent) {
        // TODO
    }

    private fun onTouchCancel(event: MotionEvent) {
        // TODO
    }

    private fun onTouchPointUp(event: MotionEvent) {
        // TODO
    }

    private fun findPanelByLocation(x: Float, y: Float): Panel<*>? {
        for (panel in priorityList) {
            panel.view?.let { child ->
                if (child.left <= x && child.right >= x
                    && child.top <= y && child.bottom >= y) {
                    return panel
                }
            }
        }
        return null
    }

    private fun touchInSelectedPanel(x: Int, y: Int): Boolean {
        logger("touchInSelectedPanel")
        dragMode = DragMode.None
        val rect = getRect()
        val panel = selectedPanel ?: return false
        panel.copyBoundsByPixels(rect)
        // 加上偏移量，应对拖拽场景
        rect.offset(panel.translationX.toInt(), panel.translationY.toInt())
        rect.selfCheck()
        logger("touchInSelectedPanel, bounds:$rect, point:[$x,$y]")
        if (rect.isEmpty) {
            rect.recycle()
            return false
        }
        val touchR = dragStrokeWidth / 2
        // 是否点击在了左侧拖拽范围
        // X在左侧边缘的有效范围内，并且Y在面板的高度范围内
        if (x < rect.left + touchR && x > rect.left - touchR
            && y > rect.top + touchR && y < rect.bottom - touchR
        ) {
            dragMode = DragMode.Left
            rect.recycle()
            return true
        }

        // 是否点击在了上侧拖拽范围
        // X在面板的宽度范围内，并且Y在上侧边缘的有效范围内
        if (x > rect.left + touchR && x < rect.right - touchR
            && y > rect.top - touchR && y < rect.top + touchR
        ) {
            dragMode = DragMode.Top
            rect.recycle()
            return true
        }

        // 是否点击在了右侧拖拽范围
        // X在右侧边缘的有效范围内，并且Y在面板的高度范围内
        if (x < rect.right + touchR && x > rect.right - touchR
            && y > rect.top + touchR && y < rect.bottom - touchR
        ) {
            dragMode = DragMode.Right
            rect.recycle()
            return true
        }

        // 是否点击在了下侧拖拽范围
        // X在面板的宽度范围内，并且Y在下侧边缘的有效范围内
        if (x > rect.left + touchR && x < rect.right - touchR
            && y > rect.bottom - touchR && y < rect.bottom + touchR
        ) {
            dragMode = DragMode.Bottom
            rect.recycle()
            return true
        }

        // 如果都不符合，那么尝试检查是否在面板的范围内
        // 如果在，那么就进入移动模式
        if (rect.contains(x, y)) {
            dragMode = DragMode.Move
            rect.recycle()
            return true
        }

        rect.recycle()
        return false
    }

    /**
     * 矩形的自检方法
     * 当右边小于左边时，交换左右位置，
     * 使面积及形状不变，但是尺寸正确
     */
    private fun Rect.selfCheck() {
        if (this.left > this.right) {
            val tmp = this.right
            this.right = this.left
            this.left = tmp
        }
        if (this.top > this.bottom) {
            val tmp = this.bottom
            this.bottom = this.top
            this.top = tmp
        }
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

    private fun getRect(): Rect {
        if (recyclerRectList.isEmpty()) {
            return Rect()
        }
        return recyclerRectList.removeFirst()
    }

    private fun Rect.recycle() {
        recyclerRectList.add(this)
    }


}