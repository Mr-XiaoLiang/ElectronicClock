package liang.lollipop.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import liang.lollipop.widget.info.SystemWidgetPanelInfo
import liang.lollipop.widget.utils.AppWidgetHelper
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
class WidgetHelper private constructor(activity: Activity,
    private val widgetGroup: WidgetGroup) {

    companion object {
        fun with(context: Activity, group: WidgetGroup): WidgetHelper {
            return WidgetHelper(context, group)
        }

        private const val DEF_MAX_LIGHT = 200F
    }

    /**
     * 应用上下文
     */
    private val context = activity.applicationContext

    private val logger = Utils.loggerI("WidgetHelper")

    /**
     * 边框绘制画笔
     */
    private val strokePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
        }
    }

    /**
     * 选中的颜色
     */
    var selectedColor = Color.RED

    /**
     * 焦点高亮色
     */
    var focusColor = Color.WHITE

    /**
     * 拖拽小点的半径
     */
    var touchPointRadius = widgetGroup.resources.dp(5F)

    /**
     * 拖拽的有效范围
     */
    var dragStrokeWidth: Float
        set(value) {
            widgetGroup.dragStrokeWidth = value
        }
        get() {
            return widgetGroup.dragStrokeWidth
        }

    /**
     * 选中边框的展示宽度
     */
    var selectedBorderWidth = widgetGroup.resources.dp(1F)

    /**
     * 临时边界，用于绘制边框时的计算
     * 减少不必要的创建对象
     */
    private val tmpBounds = Rect()

    /**
     * 面板列表的集合
     */
    private val panelList = ArrayList<Panel<*>>()

    /**
     * 是否允许拖拽
     * 当为false的时候，将拒绝长按时间
     */
    var canDrag = true

    /**
     * 延迟排版时间
     */
    var pendingLayoutTime = -1L

    /**
     * 前景色
     */
    var foregroundColor: Int = Color.WHITE
        set(value) {
            field = value
            onColorChange()
        }

    /**
     * 背景色
     */
    var backgroundColor: Int = Color.BLACK
        set(value) {
            field = value
            onColorChange()
        }

    /**
     * 亮度的最大阈值
     */
    var lightMaxValue = DEF_MAX_LIGHT
        set(value) {
            field = value
            lightValue = value
        }

    /**
     * 是否自动亮度
     */
    var isAutoLight = true
        set(value) {
            field = value
            onLightChange()
        }

    /**
     * 是否反色
     */
    var isInverted = false
        set(value) {
            field = value
            onLightChange()
        }

    /**
     * 是否自动反色
     */
    var isAutoInverted = true

    /**
     * 是否是竖屏
     */
    private var isPortrait = true

    /**
     * 上一分钟，用于选择性的懒更新部分小部件
     */
    private var lastMinute = 0L

    /**
     * 亮度值
     * 用于保存和触发亮度更新
     * 当亮度值没有变化时，不会触发亮度变化
     */
    private var lightValue = lightMaxValue
        set(value) {
            val lastValue = field
            field = value
            if (isAutoLight && lastValue != value) {
                onLightChange()
            }
        }

    /**
     * 用于做延时任务的Handler
     */
    private val handler = Handler()

    /**
     * 每秒更新的任务，用于每秒触发一次页面更新任务
     */
    private val updateTask = Runnable {
        val minute = System.currentTimeMillis() / 1000 / 60
        if (isAutoInverted) {
            isInverted = minute / 60 % 2 == 0L
        }
        if (minute != lastMinute) {
            lastMinute = minute
            updateByMinute()
        } else {
            updateBySecond()
        }
        postUpdate()
    }

    /**
     * 当取消拖拽时的监听函数
     */
    private var onCancelDragListener: ((Panel<*>?) -> Unit)? = null

    /**
     * 开始拖拽的监听事件
     */
    private var onStartDragListener: ((Panel<*>) -> Unit)? = null

    /**
     * 系统小部件辅助包装类
     */
    private val appWidgetHelper = AppWidgetHelper(activity).apply {
        onWidgetCreate {
            addPanel(it)
        }
    }

    /**
     * 面板组件适配器
     */
    private val panelAdapter = PanelAdapter(appWidgetHelper)

    /**
     * 亮度传感器的监听器
     */
    private val lightSensorListener = object: SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            // 当无法获取数据时，使用最高值
            lightValue = event?.values?.get(0)?:lightMaxValue
        }
    }

    init {
        // 默认设置绘制状态监听器，处理选中项效果绘制
        widgetGroup.onDrawSelectedPanel { panel, dragMode, canvas ->
            drawPanelBounds(panel, dragMode, canvas)
        }
        // 设置默认的长按事件
        onChildLongClick {
            logger("onChildLongClick: $it")
            if (canDrag) {
                startDrag(it)
                true
            } else {
                false
            }
        }
        // 设置默认的取消拖拽事件，并且触发一次排版事件
        widgetGroup.onCancelDrag {
            (it?.view?:widgetGroup).requestLayout()
            onCancelDragListener?.invoke(it)
        }
    }

    fun selectAppWidget() {
        appWidgetHelper.selectAppWidget()
    }

    fun onSelectWidgetError(lis: () -> Unit): WidgetHelper {
        appWidgetHelper.onSelectWidgetError(lis)
        return this
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return appWidgetHelper.onActivityResult(requestCode, resultCode, data)
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

    fun onStartDrag(lis: (Panel<*>) -> Unit): WidgetHelper {
        onStartDragListener = lis
        return this
    }

    fun onChildLongClick(lis: (Panel<*>) -> Boolean): WidgetHelper {
        widgetGroup.onChildLongClick(lis)
        return this
    }

    fun onSelectedPanelChange(lis: (Panel<*>) -> Unit): WidgetHelper {
        widgetGroup.onSelectedPanelChange(lis)
        return this
    }

    fun startDrag(panel: Panel<*>) {
        widgetGroup.selectedPanel = panel
        onStartDragListener?.invoke(panel)
    }

    fun onStart() {
        registerLightSensor()
        postUpdate()
        appWidgetHelper.onStart()
    }

    fun onStop() {
        unregisterLightSensor()
        handler.removeCallbacks(updateTask)
        appWidgetHelper.onStop()
    }

    private fun registerLightSensor() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (sensor != null) {
            sensorManager.registerListener(lightSensorListener, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun unregisterLightSensor() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(lightSensorListener)
    }

    private fun onLightChange() {
        onColorChange()
    }

    private fun onColorChange() {
        val bg: Int
        val fg: Int
        if (isInverted) {
            fg = backgroundColor
            bg = foregroundColor.updateColorByLight()
        } else {
            bg = backgroundColor
            fg = foregroundColor.updateColorByLight()
        }
        panelList.forEach {
            it.panelInfo.color = fg
            it.onColorChange(fg)
        }
        widgetGroup.setBackgroundColor(bg)
    }

    private fun Int.updateColorByLight(): Int {
        if (isAutoLight) {
            var alpha = (191 * lightValue / lightMaxValue).toInt() + 64
            if (alpha < 0) {
                alpha = 0
            }
            if (alpha > 255) {
                alpha = 255
            }
            return this and 0xFFFFFF or (alpha shl 24)
        }
        return this
    }

    private fun Int.superimposed(color: Int): Int {
        val alpha1 = Color.alpha(this).colorWeight()
        val alpha2 = Color.alpha(color).colorWeight()
        val alphaBlend = ((alpha1 + alpha2 - alpha1 * alpha2) * 255).toInt()

        val red1 = Color.red(this).colorWeight()
        val red2 = Color.red(color).colorWeight()
        val redBlend = (mixingColor(alpha1, alpha2, red1, red2) * 255).toInt()

        val green1 = Color.green(this).colorWeight()
        val green2 = Color.green(color).colorWeight()
        val greenBlend = (mixingColor(alpha1, alpha2, green1, green2) * 255).toInt()

        val blue1 = Color.blue(this).colorWeight()
        val blue2 = Color.blue(color).colorWeight()
        val blueBlend = (mixingColor(alpha1, alpha2, blue1, blue2) * 255).toInt()
        return Color.argb(alphaBlend, redBlend, greenBlend, blueBlend)
    }

    /**
     * 根据透明度混合单一通道的颜色
     * @param a1 第一个通道的透明度
     * @param a2 第二个通道的透明度
     * @param c1 第一个通道颜色值的分量，value / 255
     * @param c2 第二个通道颜色值的分量，value / 255
     */
    private fun mixingColor(a1: Float, a2: Float, c1: Float, c2: Float): Float {
        return (c1 * a1 * (1.0F - a2) + c2 * a2) / (a1 + a2 - a1 * a2)
    }

    private fun Int.colorWeight(): Float {
        return this / 255F
    }

    private fun postUpdate() {
        handler.postDelayed(updateTask, 1000)
    }

    private fun updateBySecond() {
        panelList.forEach {
            if (panelAdapter.updateBySecond(it)) {
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
        val panel = panelAdapter.createPanelByInfo(info)
        addPanel(panel)
        return panel
    }

    fun addPanel(panel: Panel<*>) {
        panel.panelInfo.color = foregroundColor
        panelList.add(panel)
        widgetGroup.addPanel(panel)
    }

    fun removePanel(panel: Panel<*>) {
        panel.panelInfo.let { info ->
            if (info is SystemWidgetPanelInfo) {
                appWidgetHelper.deleteWidgetByInfo(info)
            }
        }
        widgetGroup.removePanel(panel)
        panelList.remove(panel)
    }

    fun removeSelectedPanel() {
        widgetGroup.selectedPanel?.let { panel ->
            removePanel(panel)
        }
    }

}