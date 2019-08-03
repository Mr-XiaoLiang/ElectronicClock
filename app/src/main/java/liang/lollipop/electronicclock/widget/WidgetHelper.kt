package liang.lollipop.electronicclock.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import liang.lollipop.electronicclock.utils.Utils
import liang.lollipop.electronicclock.utils.dp

/**
 * @author lollipop
 * @date 2019-07-30 20:41
 * 小部件辅助器
 */
class WidgetHelper(private val widgetGroup: WidgetGroup) {

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

    private val tmpBounds = Rect()

    val panelList = ArrayList<Panel<*>>()

    var canDrag = true

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
    }

    fun onChildClick(lis: (Panel<*>) -> Unit) {
        widgetGroup.onChildClick(lis)
    }

    fun onCantLayout(lis: (Panel<*>) -> Unit) {
        widgetGroup.onCantLayout(lis)
    }

    fun onCancelDrag(lis: (Panel<*>?) -> Unit) {
        widgetGroup.onCancelDrag(lis)
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
        strokePaint.strokeWidth = dragStrokeWidth
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
        panelList.add(panel)
        widgetGroup.addPanel(panel)
        return panel
    }

    fun removePanel(panel: Panel<*>) {
        widgetGroup.removePanel(panel)
        panelList.remove(panel)
    }

}