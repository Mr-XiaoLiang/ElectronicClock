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

    init {
        widgetGroup.onDrawSelectedPanel { panel, dragMode, canvas ->
            drawPanelBounds(panel, dragMode, canvas)
        }
        widgetGroup.onChildLongClick {
            widgetGroup.selectedPanel = it
            logger("onChildLongClick: $it")
            true
        }
    }

    /**
     * 绘制面板的轮廓
     * @param panel 面板对象，用于获取一些状态
     * @param dragMode 拖拽模式，用于调整绘制的显示效果
     * @param canvas Group的绘制画板，可以选择直接绘制，或者其他方式显示
     */
    private fun drawPanelBounds(panel: Panel<*>, dragMode: WidgetGroup.DragMode, canvas: Canvas) {
        panel.copyBoundsByPixels(tmpBounds)
        tmpBounds.offset(panel.translationX.toInt(), panel.translationY.toInt())
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Move) { focusColor } else { selectedColor }
        canvas.drawRect(tmpBounds, strokePaint)
        strokePaint.style = Paint.Style.FILL_AND_STROKE
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Left) { focusColor } else { selectedColor }
        canvas.drawCircle(tmpBounds.left.toFloat(), tmpBounds.exactCenterY(), touchPointRadius, strokePaint)
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Right) { focusColor } else { selectedColor }
        canvas.drawCircle(tmpBounds.right.toFloat(), tmpBounds.exactCenterY(), touchPointRadius, strokePaint)
        strokePaint.color = if (dragMode == WidgetGroup.DragMode.Top) { focusColor } else { selectedColor }
        canvas.drawCircle(tmpBounds.exactCenterX(), tmpBounds.top.toFloat(), touchPointRadius, strokePaint)
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