package liang.lollipop.electronicclock.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import liang.lollipop.electronicclock.utils.Utils
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
     * 单元格的尺寸
     */
    private var gridSize = EMPTY_SIZE

    /**
     * 临时的矩形对象
     * 用于辅助计算
     */
    private val tmpRect1 = Rect()
    /**
     * 临时的矩形对象
     * 用于辅助计算
     */
    private val tmpRect2 = Rect()
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
    private var cantLayoutPanelListener: ((Panel<*>) -> Unit)? = null

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
     * 添加面板
     * 添加View的唯一途径
     * Group需要根据panel的一些参数决定小部件怎么进行布局排版
     */
    fun addPanel(panel: Panel<*>): Boolean {
        // 如果格子数据不为空，那么认为已经有测量结果了
        // 在添加前做位置检查，减少不必要的操作
        if (!gridSize.isEmpty()) {
            val info = panel.panelInfo
            if (info.width < 1 || info.height < 1 || info.x < 0 || info.y < 0) {
                val location = findGrid(info.spanX, info.spanY)
                if (!location.isEffective()) {
                    return false
                }
            } else {
                tmpRect1.set(info.x, info.y, info.x + info.width, info.y + info.height)
                if (!canPlace(tmpRect1)) {
                    return false
                }
            }
        }
        if (panel.view == null) {
            panel.create(layoutInflater, this)
        }
        panelList.add(panel)
        addView(panel.view)
        return true
    }

    /**
     * 移除一个面板
     */
    fun removePanel(panel: Panel<*>) {
        // 如果在集合中成功移除了面板，那么也从View中移除相应的View
        if (panelList.remove(panel)) {
            super.removeView(panel.view)
        }
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
            val info = panel.panelInfo
            if (info.width < 1 || info.height < 1) {
                view.measure(MeasureSpec.makeMeasureSpec(info.spanX * gridSize.width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(info.spanY * gridSize.height, MeasureSpec.EXACTLY))
            } else {
                view.measure(MeasureSpec.makeMeasureSpec(info.width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(info.height, MeasureSpec.EXACTLY))
            }
        }
        setMeasuredDimension(srcWidth, srcHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val srcWidth = right - left
        val srcHeight = bottom - top
        val widthSize = srcWidth - paddingLeft - paddingRight
        val heightSize = srcHeight - paddingTop - paddingBottom
        if (widthSize < 1 || heightSize < 1) {
            // 如果尺寸是空的，那么就放弃本次排版
            return
        }
        if (gridSize.isEmpty() || measuredWidth != srcWidth || measuredHeight != srcHeight) {
            // 如果格子尺寸是空的，那么尝试做尺寸计算
            calculateGridSize(widthSize, heightSize)
        }
        val offsetX = paddingLeft
        val offsetY = paddingTop

        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val panel = findPanelByView(view)
            // 如果View是隐藏的，那么跳过
            if (panel.visibility == View.GONE) {
                panel.layout(0, 0, 0, 0)
                continue
            }
            val info = panel.panelInfo
            if (info.width < 1 || info.height < 1 || info.x < 0 || info.y < 0) {
                // 如果位置信息不完整，那么开始尝试排版
                val location = findGrid(info.spanX, info.spanY)
                if (location.isEffective()) {
                    val panelWidth = info.spanX * gridSize.width
                    val panelHeight = info.spanY * gridSize.height
                    val x = location.x * gridSize.width + offsetX
                    val y = location.y * gridSize.height + offsetY
                    panel.layout(x, y, panelWidth + x, panelHeight + y)
                } else {
                    // 如果不能正确放置，那么将面板移至待处理列表，待排版任务结束后再处理
                    panelList.remove(panel)
                    pendingRemoveList.add(panel)
                }
            } else {
                // 如果有排版位置了，那么直接进行相应位置的排版
                panel.layout(info.x, info.y, info.width + info.x, info.height + info.y)
                if (!canPlace(panel)) {
                    // 如果不能正确放置，那么将面板移至待处理列表，待排版任务结束后再处理
                    panelList.remove(panel)
                    pendingRemoveList.add(panel)
                }
            }
        }
        for (p in pendingRemoveList) {
            removeViewInLayout(p.view)
            cantLayoutPanel(p)
        }
        pendingRemoveList.clear()
    }

    /**
     * 找到一个符合要求的格子，并且返回它的坐标
     * 返回的坐标为格子的左上角格子坐标
     */
    private fun findGrid(spanX: Int, spanY: Int, panel: Panel<*>? = null): Point {
        // 遍历每一个格子，直到找到一个符合要求的位置
        tmpRect1.set(0, 0, spanX * gridSize.width, spanY * gridSize.height)
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
                tmpRect1.offsetTo(x * gridSize.width, y * gridSize.height)
                tmpRect1.offset(paddingLeft, paddingTop)
                // 每次检查前，默认为当前格子符合要求
                meet = true
                for (p in panelList) {
                    // 如果是自身，那么跳过检查
                    if (p == panel) {
                        continue
                    }
                    p.copyBounds(tmpRect2)
                    // 如果和某一个面板存在交集，那么认为这个位置不可用
                    // 结束检查，开始下一轮的循环
                    if (hasIntersection(tmpRect1, tmpRect2)) {
                        meet = false
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
        return tmpPoint
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (Utils.isDebug) {
            canvas?:return
            val gridPaint = Paint()
            gridPaint.color = Color.RED
            for (x in 0 until spanCountX) {
                val left = (x * gridSize.width).toFloat()
                canvas.drawLine(left, 0F, left, height.toFloat(), gridPaint)
            }
            for (y in 0 until spanCountY) {
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
    private fun canPlace(panel: Panel<*>): Boolean {
        panel.copyBounds(tmpRect1)
        return canPlace(tmpRect1)
    }

    /**
     * 是否可以放置
     * 检查面板是否可以被放置
     * @return 如果为true，表示可以被放置
     */
    private fun canPlace(rect: Rect): Boolean {
        rect.selfCheck()
        if (rect.isEmpty || rect.left < 0 || rect.top < 0) {
            return false
        }
        for (p in panelList) {
            if (p.visibility == View.GONE) {
                continue
            }
            p.copyBounds(tmpRect1)
            if (hasIntersection(tmpRect1, rect)) {
                return false
            }
        }
        return true
    }

    /**
     * 两个面板之间是否存在交集
     * 如果存在交集，那么返回true
     */
    private fun hasIntersection(p0: Panel<*>, p1: Panel<*>): Boolean {
        // 复制得到面板对应的View尺寸及位置
        p0.copyBounds(tmpRect1)
        p1.copyBounds(tmpRect2)
        return hasIntersection(tmpRect1, tmpRect2)
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
    private fun cantLayoutPanel(panel: Panel<*>) {
        cantLayoutPanelListener?.invoke(panel)
    }

    /**
     * 监听布局失败的场景
     */
    fun onCantLayout(listener: (Panel<*>) -> Unit) {
        cantLayoutPanelListener = listener
    }

}