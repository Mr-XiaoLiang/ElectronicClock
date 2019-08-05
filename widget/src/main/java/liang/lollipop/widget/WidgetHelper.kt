package liang.lollipop.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.utils.dp
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelAdapter
import liang.lollipop.widget.widget.PanelInfo
import liang.lollipop.widget.widget.WidgetGroup

/**
 * @author lollipop
 * @date 2019-07-30 20:41
 * 小部件辅助器
 */
class WidgetHelper private constructor(private val widgetGroup: WidgetGroup) {

    companion object {
        fun with(group: WidgetGroup): WidgetHelper {
            return WidgetHelper(group)
        }
    }

    private val logger = Utils.loggerI("WidgetHelper")

    private val strokePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    var selectedColor = Color.RED

    var focusColor = Color.WHITE

    var touchPointRadius = widgetGroup.resources.dp(5F)

    var dragStrokeWidth: Float
        set(value) {
            widgetGroup.dragStrokeWidth = value
        }
        get() {
            return widgetGroup.dragStrokeWidth
        }

    var selectedBorderWidth = widgetGroup.resources.dp(1F)

    private val tmpBounds = Rect()

    val panelList = ArrayList<Panel<*>>()

    var canDrag = true

    var pendingLayoutTime = -1L

    var foregroundColor: Int = Color.BLACK
        set(value) {
            field = value
            onColorChange()
        }

    private var lastMinute = 0L

    private val handler = Handler()

    private val updateTask = Runnable {
        val minute = System.currentTimeMillis() / 1000 / 60
        if (minute != lastMinute) {
            lastMinute = minute
            updateByMinute()
        } else {
            updateBySecond()
        }
        postUpdate()
    }

    private var onCancelDragListener: ((Panel<*>?) -> Unit)? = null

    init {
        widgetGroup.onDrawSelectedPanel { panel, dragMode, canvas ->
            drawPanelBounds(panel, dragMode, canvas)
        }
        widgetGroup.onChildLongClick {
            logger("onChildLongClick: $it")
            if (canDrag) {
                widgetGroup.selectedPanel = it
                true
            } else {
                false
            }
        }
        widgetGroup.onCancelDrag {
            (it?.view?:widgetGroup).requestLayout()
            onCancelDragListener?.invoke(it)
        }
    }

    fun onChildClick(lis: (Panel<*>) -> Unit): WidgetHelper {
        widgetGroup.onChildClick(lis)
        return this
    }

    fun onCantLayout(lis: (Array<Panel<*>>) -> Unit): WidgetHelper {
        widgetGroup.onCantLayout(lis)
        return this
    }

    fun onCancelDrag(lis: (Panel<*>?) -> Unit): WidgetHelper {
        onCancelDragListener = lis
        return this
    }

    fun onChildLongClick(lis: (Panel<*>) -> Boolean): WidgetHelper {
        widgetGroup.onChildLongClick(lis)
        return this
    }

    fun startDrag(panel: Panel<*>) {
        widgetGroup.selectedPanel = panel
    }

    fun onStart() {
        postUpdate()
    }

    fun onStop() {
        handler.removeCallbacks(updateTask)
    }

    private fun onColorChange() {
        panelList.forEach {
            it.panelInfo.color = foregroundColor
            it.onColorChange(foregroundColor)
        }
    }

    private fun postUpdate() {
        handler.postDelayed(updateTask, 1000)
    }

    private fun updateBySecond() {
        panelList.forEach {
            if (PanelAdapter.updateBySecond(it)) {
                it.onUpdate()
            }
        }
    }

    private fun updateByMinute() {
        panelList.forEach {
            it.onUpdate()
        }
    }

    /**
     * 绘制面板的轮廓
     * @param panel 面板对象，用于获取一些状态
     * @param dragMode 拖拽模式，用于调整绘制的显示效果
     * @param canvas Group的绘制画板，可以选择直接绘制，或者其他方式显示
     */
    private fun drawPanelBounds(panel: Panel<*>, dragMode: WidgetGroup.DragMode, canvas: Canvas) {
        // 复制边框位置
        panel.copyBoundsByPixels(tmpBounds)
        // 为显示的偏移量做校准
        tmpBounds.offset(panel.translationX.toInt(), panel.translationY.toInt())
        // 绘制边框，设置为描边模式
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Move) { focusColor } else { selectedColor }
        strokePaint.strokeWidth = selectedBorderWidth
        canvas.drawRect(tmpBounds, strokePaint)
        // 开始绘制四个拖动点，使用填充和描边方式
        strokePaint.style = Paint.Style.FILL_AND_STROKE
        strokePaint.strokeWidth = 1F
        // 左侧
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Left) { focusColor } else { selectedColor }
        canvas.drawCircle(tmpBounds.left.toFloat(), tmpBounds.exactCenterY(), touchPointRadius, strokePaint)
        // 右侧
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Right) { focusColor } else { selectedColor }
        canvas.drawCircle(tmpBounds.right.toFloat(), tmpBounds.exactCenterY(), touchPointRadius, strokePaint)
        // 顶部
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Top) { focusColor } else { selectedColor }
        canvas.drawCircle(tmpBounds.exactCenterX(), tmpBounds.top.toFloat(), touchPointRadius, strokePaint)
        // 底部
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Bottom) { focusColor } else { selectedColor }
        canvas.drawCircle(tmpBounds.exactCenterX(), tmpBounds.bottom.toFloat(), touchPointRadius, strokePaint)
    }

    fun <I: PanelInfo> addPanel(info: I): Panel<I> {
        val panel = PanelAdapter.createPanelByInfo(info)
        addPanel(panel)
        return panel
    }

    fun addPanel(panel: Panel<*>) {
        panel.panelInfo.color = foregroundColor
        panelList.add(panel)
        widgetGroup.addPanel(panel)
    }

    fun removePanel(panel: Panel<*>) {
        widgetGroup.removePanel(panel)
        panelList.remove(panel)
    }

}