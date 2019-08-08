package liang.lollipop.widget.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.InflateException
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.utils.dp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

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

    companion object {
        /**
         * 空的尺寸，默认为容器未完成排版的时候初始值
         */
        private val EMPTY_SIZE = Size(0, 0)

        private val logger = Utils.loggerI("WidgetGroup")

        private const val CANCEL_MODE_TIME = 300L

        private const val NO_ID = -1
    }

    /**
     * 面板组件的集合
     * 用于保存面板对象，从中获取数据
     */
    private val panelList = ArrayList<Panel<*>>()
    /**
     * 待移除面板的集合
     * 当布局过程中，发现无法添加的面板，将会添加到这里，然后移除
     */
    private val pendingRemoveList = ArrayList<Panel<*>>()

    /**
     * 用于实例化Panel中View的上下文
     */
    private val layoutInflater = LayoutInflater.from(context)

    /**
     * 回收复用的Rect对象集合
     */
    private val recyclerRectList = LinkedList<Rect>()

    /**
     * 单元格的尺寸
     */
    var gridSize = EMPTY_SIZE
        private set

    /**
     * 临时的点对象
     * 用于辅助计算
     */
    private val tmpPoint = Point()

    /**
     * 横向的格数
     * 这是通过{#gridCount}计算出来的横向格数
     */
    private var spanCountX = 0
    /**
     * 纵向的格数
     * 这是通过{#gridCount}计算出来的纵向格数
     */
    private var spanCountY = 0

    /**
     * 无法进行排列的面板的通知函数
     */
    private var cantLayoutPanelListener: ((panels: Array<Panel<*>>) -> Unit)? = null

    /**
     * 用来绘制选中面板边框效果的回调函数
     */
    private var drawSelectedPanelListener: ((panel: Panel<*>, mode: DragMode, canvas: Canvas) -> Unit)? = null

    /**
     * 子View的长按点击事件
     */
    private var childLongClickListener: ((panel: Panel<*>) -> Boolean)? = null

    /**
     * 子View的点击事件
     */
    private var childClickListener: ((panel: Panel<*>) -> Unit)? = null

    /**
     * 取消拖拽的监听器
     */
    private var cancelDragListener: ((panel: Panel<*>?) -> Unit)? = null

    /**
     * 拖拽结束后的监听器
     */
    private var dragEndListener: ((panel: Panel<*>?) -> Unit)? = null

    /**
     * 当被选中的面板改变时触发的监听器
     */
    private var onSelectedPanelChangeListener: ((panel: Panel<*>) -> Unit)? = null

    /**
     * 格子数量
     * 这个数量是指窄边的格子数
     * 长边的数量会依据此数量来进行变化
     * 尽量保证格子更加接近正方形
     */
    var gridCount = 6
        set(value) {
            field = value
            gridSize = EMPTY_SIZE
            requestLayout()
        }

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

    private var activeActionId = NO_ID

    /**
     * 按下位置
     */
    private var touchDown = PointF()

    /**
     * 拖拽模式
     */
    private var dragMode = DragMode.None

    /**
     * 拖拽的边框宽度，用于调整触发灵敏度
     */
    var dragStrokeWidth = resources.dp(5F)

    /**
     * 请求一次手势拦截
     */
    private var pendingTouchRequest = false

    /**
     * 锁定拖动和尺寸改变
     */
    var lockedGrid = false

    /**
     *
     */
    var lockedTouch = false

    /**
     * 上次的Padding
     */
    private val lastPadding = Rect()

    /**
     * 上次的尺寸
     */
    private val lastBounds = Rect()

    /**
     * 绘制格子
     */
    var drawGrid = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 绘制格子用的笔
     */
    private val gridPaint: Paint by lazy {
        Paint().apply {
            isDither = true
            isAntiAlias = true
        }
    }

    /**
     * 绘制格子
     */
    var gridColor: Int
        set(value) {
            if (drawGrid) {
                gridPaint.color = value
            }
        }
        get() {
            return gridPaint.color
        }

    /**
     * 添加面板
     * 添加View的唯一途径
     * Group需要根据panel的一些参数决定小部件怎么进行布局排版
     */
    fun addPanel(panel: Panel<*>): Boolean {
        // 如果格子数据不为空，那么认为已经有测量结果了
        // 在添加前做位置检查，减少不必要的操作
        if (!gridSize.isEmpty()) {
            panel.updatePanelInfo(this)
            val info = panel.panelInfo
            val location = findGrid(info.spanX, info.spanY)
            if (!location.isEffective()) {
                return false
            }
        }
        if (panel.view == null) {
            panel.create(layoutInflater, this)
        }
        panelList.add(panel)
        addView(panel.view)
        setChildLongClick(panel)
        setChildClick(panel)
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
    }

    override fun performClick(): Boolean {
        return if (isDragState && dragMode != DragMode.None) { false } else { super.performClick() }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?:return super.onInterceptTouchEvent(event)
        // 如果不在拖拽模式，那么放弃手势处理
        if (!isDragState || dragMode == DragMode.None || activeActionId == NO_ID) {
            return super.onInterceptTouchEvent(event)
        }
        when (event.actionMasked){
            MotionEvent.ACTION_MOVE -> {
                val result = onDrag(event.getXById() - touchDown.x, event.getYById() - touchDown.y)
                if (!result) {
                    logger("onTouchEvent, ACTION_MOVE, onDrag selectedPanel not found, cancelSelected")
                    cancelSelected()
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                logger("onTouchEvent, ACTION_UP")
                // 完成了拖拽，但是符合操作要求，因此保持操作状态
                endDrag()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                logger("onTouchEvent, ACTION_POINTER_UP")
                if (onPointerUp(event)) {
                    return true
                }
            }
        }
        if (interceptBySelectedMode(event)) {
            return true
        }
        return (isDragState && dragMode != DragMode.None) || super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (lockedTouch) {
            return true
        }
        if (lockedGrid) {
            return false
        }
        ev?:return super.onInterceptTouchEvent(ev)
        // 如果不在拖拽模式，那么放弃拦截任何手势
        if (!isDragState) {
            logger("onInterceptTouchEvent, isDragState = false, break")
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                logger("onInterceptTouchEvent, ACTION_DOWN")
                pendingTouchRequest = false
                // 检查按下事件是否符合要求，如果符合，那么认为开始拖拽
                touchDown.set(ev.x, ev.y)
                if (touchInSelectedPanel(touchDown.x.toInt(), touchDown.y.toInt())) {
                    activeActionId = ev.getPointerId(0)
                    logger("onInterceptTouchEvent, ACTION_DOWN -> activeActionId = $activeActionId, dragMode = $dragMode")
                } else {
                    cancelSelected()
                    logger("onInterceptTouchEvent, ACTION_DOWN -> cancelSelected()")
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                logger("onInterceptTouchEvent, ACTION_POINTER_UP")
                if (onPointerUp(ev)) {
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                logger("onInterceptTouchEvent, ACTION_UP")
                // 完成了拖拽，但是符合操作要求，因此保持操作状态
                endDrag()
            }
            MotionEvent.ACTION_CANCEL -> {
                logger("onInterceptTouchEvent, ACTION_CANCEL")
                // 事件被拦截，取消操作状态
                cancelSelected()
                return true
            }
        }
        if (pendingTouchRequest) {
            onPointerUp(ev)
        }
        if (interceptBySelectedMode(ev)) {
            return true
        }
        // 只要是拖拽的激活状态，那么就必须要拦截全部手势
        return isDragState || super.onInterceptTouchEvent(ev)
    }

    private fun onDrag(offsetX: Float, offsetY: Float): Boolean {
        val panel = selectedPanel?:return false
        val offX = offsetX.toInt()
        val offY = offsetY.toInt()
        when (dragMode) {
            DragMode.Move -> {
                val rect = getRect()
                panel.copyBounds(rect)
                val p = moveCheck(rect, offX, offY, panel)
                if (p.x != 0 || p.y != 0) {
                    panel.offsetByGrid(p.x, p.y)
                    touchDown.x += p.x * gridSize.width
                    touchDown.y += p.y * gridSize.height
                }
                rect.recycle()
            }
            DragMode.Left -> {
                val x = round(1F * offX / gridSize.width)
                if (x != 0) {
                    val rect = getRect()
                    panel.copyBounds(rect)
                    rect.left += x
                    val info = panel.panelInfo
                    if (info.spanX - x > 0 && canPlace(rect, panel)) {
                        panel.layoutByGrid(info.x + x, info.y, info.spanX - x, info.spanY)
                        touchDown.x += x * gridSize.width
                    }
                    rect.recycle()
                }
            }
            DragMode.Right -> {
                val x = round(1F * offX / gridSize.width)
                if (x != 0) {
                    val rect = getRect()
                    panel.copyBounds(rect)
                    rect.right += x
                    val info = panel.panelInfo
                    if (info.spanX + x > 0 && canPlace(rect, panel)) {
                        panel.layoutByGrid(info.x, info.y, info.spanX + x, info.spanY)
                        touchDown.x += x * gridSize.width
                    }
                    rect.recycle()
                }
            }
            DragMode.Top -> {
                val y = round(1F * offY / gridSize.height)
                if (y != 0) {
                    val rect = getRect()
                    panel.copyBounds(rect)
                    rect.top += y
                    val info = panel.panelInfo
                    if (info.spanY - y > 0 && canPlace(rect, panel)) {
                        panel.layoutByGrid(info.x, info.y + y, info.spanX, info.spanY - y)
                        touchDown.y += y * gridSize.height
                    }
                    rect.recycle()
                }
            }
            DragMode.Bottom -> {
                val y = round(1F * offY / gridSize.height)
                if (y != 0) {
                    val rect = getRect()
                    panel.copyBounds(rect)
                    rect.bottom += y
                    val info = panel.panelInfo
                    if (info.spanY + y > 0 && canPlace(rect, panel)) {
                        panel.layoutByGrid(info.x, info.y, info.spanX, info.spanY + y)
                        touchDown.y += y * gridSize.height
                    }
                    rect.recycle()
                }
            }
            DragMode.None -> {
                logger("onDrag but dragMode is None")
            }
        }
        invalidate()
        return true
    }

    private fun round(f: Float): Int {
        return if (f >= 0) {
            (f + 0.5F).toInt()
        } else {
            (f - 0.5F).toInt()
        }
    }

    private fun moveCheck(rect: Rect, offX: Int, offY: Int, skip: Panel<*>? = null): Point {
        val p = tmpPoint
        p.x = round(1F * offX / gridSize.width)
        p.y = round(1F * offY / gridSize.height)
        if (p.x + rect.left < 0) {
            p.x = - rect.left
        }
        if (rect.right + p.x > spanCountX) {
            p.x = spanCountX - rect.right
        }
        if (rect.top + p.y < 0) {
            p.y = - rect.top
        }
        if (rect.bottom + p.y > spanCountY) {
            p.y = spanCountY - rect.bottom
        }
        val tmp = getRect()
        tmp.set(rect)
        tmp.offset(p.x, p.y)
        if (!canPlace(tmp, skip)) {
            p.set(0, 0)
        }
        tmp.recycle()
        return p
    }

    private fun onPointerUp(ev: MotionEvent): Boolean {
        logger("onPointerUp")
        // 如果有手指抬起，那么检查抬起的手指是否是我们锁定的那一个
        // 如果是，那么尝试更换另一个有效的手指
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == activeActionId || activeActionId == NO_ID) {
            // 当活跃的那个指头抬起，那么重新选定一个指头作为事件来源
            var newPointerIndex = -1
            for (i in 0 until ev.pointerCount) {
                if (pointerIndex == i && ev.actionMasked == MotionEvent.ACTION_POINTER_UP) {
                    continue
                }
                if (touchInSelectedPanel(ev.getX(i).toInt(),
                        ev.getY(i).toInt())) {
                    newPointerIndex = i
                    break
                }
            }
            // 如果没有符合条件的手指，那么放弃事件，认为本次任务结束
            if (newPointerIndex < 0) {
                cancelSelected()
                logger("onPointerUp newPointerIndex = $newPointerIndex, cancelSelected")
                return true
            }
            touchDown.set(ev.getX(newPointerIndex), ev.getY(newPointerIndex))
            activeActionId = ev.getPointerId(newPointerIndex)
            // 如果顺利完成了手指的更换，那么放置面板，并且从头开始
            pushPanelWhenTouch()
            logger("onPointerUp newPointerIndex = $newPointerIndex, pushPanelWhenTouch")
        }
        return false
    }

    private fun interceptBySelectedMode(ev: MotionEvent?): Boolean {
        if (dragMode != DragMode.None) {
            return false
        }
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchDownTime = System.currentTimeMillis()
                logger("interceptBySelectedMode, ACTION_DOWN: $touchDownTime")
            }
            MotionEvent.ACTION_UP -> {
                val diff = System.currentTimeMillis() - touchDownTime
                logger("interceptBySelectedMode, ACTION_UP diff = $diff")
                if (diff < CANCEL_MODE_TIME) {
                    cancelSelected()
                    return true
                }
            }
        }
        return false
    }

    private fun cancelSelected() {
        logger("cancelSelected")
        pushPanelWhenTouch()
        cancelDragListener?.invoke(selectedPanel)
        selectedPanel = null
        activeActionId = NO_ID
        invalidate()
    }

    private fun endDrag() {
        pushPanelWhenTouch()
        dragEndListener?.invoke(selectedPanel)
        activeActionId = NO_ID
        invalidate()
    }

    private fun pushPanelWhenTouch() {
        logger("pushPanelWhenTouch")
        pendingTouchRequest = false
        dragMode = DragMode.None
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val srcWidth = MeasureSpec.getSize(widthMeasureSpec)
        val srcHeight = MeasureSpec.getSize(heightMeasureSpec)
        val widthSize = srcWidth - paddingLeft - paddingRight
        val heightSize = srcHeight - paddingTop - paddingBottom
        calculateGridSize(widthSize, heightSize)
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val panel = findPanelByView(view)
            // 如果View是隐藏的，那么跳过
            if (panel.visibility == View.GONE) {
                panel.layout(0, 0, 0, 0)
                continue
            }
            // 测量时，刷新一次小部件的尺寸信息，便于调整到合适的尺寸
            panel.updatePanelInfo(this)
            val info = panel.panelInfo
            view.measure(MeasureSpec.makeMeasureSpec(info.spanX * gridSize.width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(info.spanY * gridSize.height, MeasureSpec.EXACTLY))
        }
        setMeasuredDimension(srcWidth, srcHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        logger("onLayout: $changed, $left, $top, $right, $bottom")
        val srcWidth = right - left
        val srcHeight = bottom - top
        val widthSize = srcWidth - paddingLeft - paddingRight
        val heightSize = srcHeight - paddingTop - paddingBottom
        if (widthSize < 1 || heightSize < 1) {
            // 如果尺寸是空的，那么就放弃本次排版
            return
        }
        val isPaddingChange = lastPadding.top != paddingTop ||
                lastPadding.left != paddingLeft ||
                lastPadding.right != paddingRight ||
                lastPadding.bottom != paddingBottom
        val isBoundsChange = lastBounds.left != left ||
                lastBounds.top != top ||
                lastBounds.right != right ||
                lastBounds.bottom != bottom
        if (gridSize.isEmpty() || measuredWidth != srcWidth || measuredHeight != srcHeight ||
            isPaddingChange || isBoundsChange) {
            // 如果格子尺寸是空的，那么尝试做尺寸计算
            calculateGridSize(widthSize, heightSize)
        }
        lastPadding.set(paddingLeft, paddingTop, paddingRight, paddingBottom)
        lastBounds.set(left, top, right, bottom)
        val tmpRect = getRect()
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val panel = findPanelByView(view)
            // 如果View是隐藏的，那么跳过
            if (panel.visibility == View.GONE) {
                panel.layout(0, 0, 0, 0)
                continue
            }
            val info = panel.panelInfo
            if (info.x < 0 || info.y < 0) {
                // 如果没有位置信息，那么尝试寻找一个位置
                val location = findGrid(info.spanX, info.spanY, panel)
                if (location.isEffective()) {
                    // 如果位置有效，那么进行赋值
                    info.offset(location.x, location.y)
                }
            }
            // 如果无法找到有效的位置，那么对View进行移除
            if (info.x < 0 || info.y < 0) {
                panelList.remove(panel)
                pendingRemoveList.add(panel)
                continue
            }
            // 有排版位置了，那么进行一次位置检查
            tmpRect.set(info.x, info.y, info.spanX + info.x, info.spanY + info.y)
            // 如果位置检查失败，认为不可用，那么就放弃
            if (canPlace(tmpRect, panel)) {
                panel.layoutByGrid(info.x, info.y, info.spanX, info.spanY)
            } else {
                // 如果不能正确放置，那么将面板移至待处理列表，待排版任务结束后再处理
                panelList.remove(panel)
                pendingRemoveList.add(panel)
            }
        }
        tmpRect.recycle()
        if (pendingRemoveList.isNotEmpty()) {
            for (p in pendingRemoveList) {
                removeViewInLayout(p.view)
            }
            cantLayoutPanel(pendingRemoveList.toTypedArray())
            pendingRemoveList.clear()
        }
    }

    private fun Panel<*>.layoutByGrid(x: Int, y: Int, spanX: Int, spanY: Int) {
        val panelWidth = spanX * gridSize.width
        val panelHeight = spanY * gridSize.height
        val left = x * gridSize.width + paddingLeft
        val top = y * gridSize.height + paddingTop
        this.layout(left, top, panelWidth + left, panelHeight + top)
        this.saveGridLocation(x, y, spanX, spanY)
        selectedPanelChange(this)
    }

    private fun Panel<*>.offsetByGrid(x: Int, y: Int) {
        if (x == 0 && y == 0) {
            return
        }
        val left = x * gridSize.width
        val top = y * gridSize.height
        this.offset(left, top)
        this.panelInfo.offsetBy(x, y)
        selectedPanelChange(this)
    }

    /**
     * 找到一个符合要求的格子，并且返回它的坐标
     * 返回的坐标为格子的左上角格子坐标
     */
    private fun findGrid(spanX: Int, spanY: Int, panel: Panel<*>? = null): Point {
        // 遍历每一个格子，直到找到一个符合要求的位置
        val rect = getRect()
        rect.set(0, 0, spanX, spanY)
        var meet: Boolean
        for (y in 0 until spanCountY) {
            // 如果超过了最大格数，那么直接返回
            if (y + spanY > spanCountY) {
                break
            }
            for (x in 0 until spanCountX) {
                // 如果超过了最大格数，那么直接返回
                if (x + spanX > spanCountX) {
                    break
                }
                // 格子尺寸不变，只做位置的偏移
                rect.offsetTo(x, y)
                // 每次检查前，默认为当前格子符合要求
                meet = true
                for (p in panelList) {
                    // 如果是自身，那么跳过检查
                    if (p == panel || p.visibility == View.GONE) {
                        continue
                    }
                    val rect2 = getRect()
                    p.copyBounds(rect2)
                    // 如果和某一个面板存在交集，那么认为这个位置不可用
                    // 结束检查，开始下一轮的循环
                    if (hasIntersection(rect, rect2)) { meet = false }
                    rect2.recycle()
                    if (!meet) {
                        break
                    }
                }
                // 如果遇见了可以使用的格子，那么提前返回
                if (meet) {
                    tmpPoint.set(x, y)
                    return tmpPoint
                }
            }
        }
        tmpPoint.set(-1, -1)
        rect.recycle()
        return tmpPoint
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?:return
        selectedPanel?.let {
            drawSelectedPanelListener?.invoke(it, dragMode, canvas)
        }
        if (drawGrid) {
            for (x in 0 .. spanCountX) {
                val left = (x * gridSize.width).toFloat()
                canvas.drawLine(left, 0F, left, height.toFloat(), gridPaint)
            }
            for (y in 0 .. spanCountY) {
                val top = (y * gridSize.height).toFloat()
                canvas.drawLine(0F, top, width.toFloat(), top, gridPaint)
            }
        }
    }

    /**
     * 计算格子尺寸
     * 将按照指定的格子数对当前容器进行栅格处理
     */
    private fun calculateGridSize(w: Int, h: Int) {
        val xSize: Int
        val ySize: Int
        // 选择窄的一边作为参照边，进行刻度计算
        // 尽量得到更接近方形的单元格
        if (w < h) {
            xSize = w / gridCount
            spanCountX = gridCount
            ySize = calculateScale(h, xSize)
            spanCountY = h / ySize
        } else {
            ySize = h / gridCount
            spanCountY = gridCount
            xSize = calculateScale(w, ySize)
            spanCountX = w / xSize
        }
        gridSize = Size(xSize, ySize)
    }

    /**
     * 对某一线段进行依据参照值的分段
     * 确保每段的长度更接近参照值
     * @return 返回每段的长度值
     */
    private fun calculateScale(length: Int, src: Int): Int {
        // 如果长度能够被参照值整除，那么直接返回参照值
        if (length % src == 0) {
            return src
        }
        // 按照参照值计算，得到一个最小的刻度数量
        val minCount = length / src
        // 如果剩余的长度超过了参照值的一半，那么认为剩余过多，那么将刻度数量+1
        // 得到最大刻度数量
        val maxCount = if (length % src > src / 2) {
            minCount + 1
        } else {
            minCount
        }
        // 如果两个刻度值相同，那么认为参照值合法，直接返回
        if (minCount == maxCount) {
            return length / minCount
        }
        // 计算两种刻度数量下的单元格长度
        val minSize = length / minCount
        val maxSize = length / maxCount
        // 当最大刻度数下的单元格剩余长度比默认情况下的少，
        // 并且单元格长度差值没有高于参照值的一半，那么认为新刻度数是合法的，
        // 记为true
        val sizeWeight = length % minSize > length % maxSize && abs(maxSize - src) < src / 2
        if (!sizeWeight) {
            // 如果最大刻度数不合法，那么使用最小刻度数下的单元格长度
            return minSize
        }
        // 最后，选择与参照值差距更小的尺寸作为结果
        return if (abs(maxSize - src) < abs(minSize - src)) {
            maxSize
        } else {
            minSize
        }
    }

    /**
     * 根据View返回panel的对象
     * 如果找不到对应的panel，那么将会抛出异常
     */
    private fun findPanelByView(view: View): Panel<*> {
        for (panel in panelList) {
            if (panel.view == view) {
                return panel
            }
        }
        throw InflateException("Found a view that does not correspond to a panel")
    }

    /**
     * 是否可以放置
     * 检查面板是否可以被放置
     * @return 如果为true，表示可以被放置
     */
    private fun canPlace(rect: Rect, skip: Panel<*>? = null): Boolean {
        rect.selfCheck()
        if (rect.isEmpty || rect.left < 0 || rect.top < 0) {
            return false
        }
        for (p in panelList) {
            if (p.visibility == View.GONE || p == skip) {
                continue
            }
            val rect2 = getRect()
            p.copyBounds(rect2)
            if (hasIntersection(rect2, rect)) {
                rect2.recycle()
                return false
            }
            rect2.recycle()
        }
        return true
    }

    /**
     * 检查两个矩形之间是否存在交集
     */
    private fun hasIntersection(r1: Rect, r2: Rect): Boolean {
        r1.selfCheck()
        r2.selfCheck()
        // 如果其中一个是空的，那么认为两者之间没有交集
        if (r1.isEmpty || r2.isEmpty) {
            return false
        }
        // left和top小于0的场景默认为未处理状态
        if (r1.left < 0 || r1.top < 0 || r2.left < 0 || r2.top < 0) {
            return false
        }
        // 判断纵向是否存在交集，如果不存在，那么表示为通过
        val vertical = r1.bottom <= r2.top || r1.top >= r2.bottom
        // 判断横向是否存在交集，如果不存在，那么表示为通过
        val horizontal = r1.right <= r2.left || r1.left >= r2.right
        // 如果横向或者纵向不存在交集，那么认为两个矩形处于平行状态
        // 否则认为有交集
        return !(vertical || horizontal)
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
     * 位置信息是否有效的检查方法
     */
    private fun Point.isEffective(): Boolean {
        return this.x >= 0 && this.y >= 0
    }

    private fun Size.isEmpty(): Boolean {
        return this.height < 1 || this.width < 1
    }

    /**
     * 当panel不能被摆放时，会将不能摆放的panel放置在此处，
     * 然后通过回调函数传出，外部可以选择调整面板尺寸
     * 或者移除此面板
     */
    private fun cantLayoutPanel(panels: Array<Panel<*>>) {
        cantLayoutPanelListener?.invoke(panels)
    }

    /**
     * 监听布局失败的场景
     */
    fun onCantLayout(listener: (Array<Panel<*>>) -> Unit) {
        cantLayoutPanelListener = listener
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

    private fun setChildLongClick(panel: Panel<*>) {
        val listener = childLongClickListener
        if (listener != null) {
            panel.view?.setOnLongClickListener{ listener(panel) }
        } else {
            panel.view?.setOnLongClickListener(null)
        }
    }

    fun onChildClick(listener: ((Panel<*>) -> Unit)?) {
        childClickListener = listener
        for (p in panelList) {
            setChildClick(p)
        }
    }

    private fun setChildClick(panel: Panel<*>) {
        val listener = childClickListener
        if (listener != null) {
            panel.view?.setOnClickListener{ listener(panel) }
        } else {
            panel.view?.setOnClickListener(null)
        }
    }

    /**
     * 绘制被选中的面板的回调
     * 将面板的选中效果抛出，由外部绘制
     */
    fun onDrawSelectedPanel(listener: (Panel<*>, DragMode, Canvas) -> Unit) {
        drawSelectedPanelListener = listener
    }

    /**
     * 取消拖拽的监听事件
     */
    fun onCancelDrag(listener: ((Panel<*>?) -> Unit)) {
        this.cancelDragListener = listener
    }

    /**
     * 触发面板变更事件
     */
    private fun selectedPanelChange(p: Panel<*>) {
        val panel = selectedPanel?:return
        if (panel == p) {
            onSelectedPanelChangeListener?.invoke(panel)
        }
    }

    /**
     * 当被选中的面板发生变化时，触发
     */
    fun onSelectedPanelChange(listener: (Panel<*>) -> Unit) {
        onSelectedPanelChangeListener = listener
    }

    /**
     * 面板的尺寸被调整时，触发的监听方法
     */
    fun onDragEndListener(listener: ((panel: Panel<*>?) -> Unit)) {
        this.dragEndListener = listener
    }

    private fun MotionEvent.getXById(): Float {
        if (activeActionId == NO_ID) {
            return 0F
        }
        return this.getX(this.findPointerIndex(activeActionId))
    }

    private fun MotionEvent.getYById(): Float {
        if (activeActionId == NO_ID) {
            return 0F
        }
        return this.getY(this.findPointerIndex(activeActionId))
    }

    private fun touchInSelectedPanel(x: Int, y: Int): Boolean {
        logger("touchInSelectedPanel")
        dragMode = DragMode.None
        val rect = getRect()
        val panel = selectedPanel?:return false
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
            && y > rect.top + touchR && y < rect.bottom - touchR) {
            dragMode = DragMode.Left
            rect.recycle()
            return true
        }

        // 是否点击在了上侧拖拽范围
        // X在面板的宽度范围内，并且Y在上侧边缘的有效范围内
        if (x > rect.left + touchR && x < rect.right - touchR
            && y > rect.top - touchR && y < rect.top + touchR) {
            dragMode = DragMode.Top
            rect.recycle()
            return true
        }

        // 是否点击在了右侧拖拽范围
        // X在右侧边缘的有效范围内，并且Y在面板的高度范围内
        if (x < rect.right + touchR && x > rect.right - touchR
            && y > rect.top + touchR && y < rect.bottom - touchR) {
            dragMode = DragMode.Right
            rect.recycle()
            return true
        }

        // 是否点击在了下侧拖拽范围
        // X在面板的宽度范围内，并且Y在下侧边缘的有效范围内
        if (x > rect.left + touchR && x < rect.right - touchR
            && y > rect.bottom - touchR && y < rect.bottom + touchR) {
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

    enum class DragMode(val value: Int) {
        None(-1),
        Move(0),
        Left(1),
        Top(2),
        Right(3),
        Bottom(4)
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