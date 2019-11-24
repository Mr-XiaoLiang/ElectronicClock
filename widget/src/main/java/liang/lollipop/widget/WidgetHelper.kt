package liang.lollipop.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.SparseArray
import liang.lollipop.widget.info.SystemWidgetPanelInfo
import liang.lollipop.widget.utils.*
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelAdapter
import liang.lollipop.widget.widget.PanelInfo
import liang.lollipop.widget.widget.WidgetGroup
import kotlin.math.max
import kotlin.math.min


/**
 * @author lollipop
 * @date 2019-07-30 20:41
 * 小部件辅助器
 */
class WidgetHelper private constructor(private val activity: Context,
                                       private val widgetGroup: WidgetGroup,
                                       private val hostId: Int = AppWidgetHelper.DEF_HOST_ID) {

    companion object {
        fun with(context: Context, group: WidgetGroup): WidgetHelper {
            return WidgetHelper(context, group)
        }

        /**
         * 设置外置的面板提供者
         * 这是一个全局的静态设置方法
         */
        fun panelProviders(panelProviders: PanelProviders?) {
            PanelAdapter.panelProviders = panelProviders
        }

        private const val DEF_MAX_LIGHT = 200F

        private const val REQUEST_CODE_PREFIX = 8 shl 12
        private const val REQUEST_CODE_MARK = 4096
    }

    private var requestCodeProgress = 0

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
    var isPortrait = true

    /**
     * 是否是编辑模式
     */
    var isInEditMode = false
        set(value) {
            field = value
            widgetGroup.isInEngineeringMode = value
        }

    /**
     * 是否是方形格子
     */
    var isSquareGrid: Boolean
        get() {
            return widgetGroup.isSquareGrid
        }
        set(value) {
            widgetGroup.isSquareGrid = value
        }

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
    private val appWidgetHelper = AppWidgetHelper(activity, hostId).apply {
        onWidgetCreate {
            addPanel(it)
        }
    }

    /**
     * 面板组件适配器
     */
    private val panelAdapter = PanelAdapter(appWidgetHelper)

    /**
     * 页面是否激活了
     */
    private var pageIsStart = false

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

    /**
     * 屏幕方向的关键字
     */
    private val directionValue: String
        get() {
            return if (isPortrait) { "PORTRAIT" } else { "LANDSCAPE" }
        }

    /**
     * 当前面板的数量
     */
    val panelCount: Int
        get() {
            return panelList.size
        }

    /**
     * 用于做延时初始化任务的info集合
     */
    private val pendlingInfoList = SparseArray<PanelInfo>()

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

        // View就绪之后，立即触发颜色
        widgetGroup.post {
            onColorChange()
        }
    }

    /**
     * 从数据库更新一次面板
     */
    fun updateByDB(statusListener: ((status: LoadStatus) -> Unit)? = null) {
        statusListener?.invoke(LoadStatus.START)
        doAsync({ uiThread { statusListener?.invoke(LoadStatus.ERROR) } }) {
            // 根据当前参数，获取一次最新的数据
            val tmpInfoList = ArrayList<PanelInfo>()
            DatabaseHelper.read(context).getOnePage(directionValue, hostId, tmpInfoList).close()
            // 记录现有数据的集合
            val existedList = ArrayList<Panel<*>>()
            existedList.addAll(panelList)
            // 新的，需要待添加的集合
            val newList = ArrayList<PanelInfo>()
            // 是否需要重新排版
            var isNeedRelayout = false
            // 添加新的小部件，移除不需要的，并且更新现有的
            tmpInfoList.forEach { info ->
                val panel = findPanelByInfo(existedList, info)
                if (panel != null) {
                    // 如果面板是存在的，并且是有效的，那么只更新数据状态
                    panel.panelInfo.copy(info)
                    if (!isNeedRelayout) {
                        isNeedRelayout = true
                    }
                    // 这是一个有意义的面板，因此从集合中移除
                    existedList.remove(panel)
                } else {
                    // 没有从现存的面板中找到有效的面板，那么加入新的集合
                    isNeedRelayout = false
                    newList.add(info)
                }
            }
            uiThread {
                // 移除了有意义的面板，现存面板中只剩下了多余的面板
                // 添加新的面板前，需要先移除现有的无效面板，防止位置占用导致的添加失败
                existedList.forEach { panel ->
                    removePanel(panel)
                }
                // 移除所有无效面板后，开始添加新的面板
                newList.forEach { info ->
                    addPanel(info)
                }
                // 如果没有小部件发生变更，那么手动触发一次排版动作
                // 因为添加或者移除面板，会自动触发面板的更新，因此不做额外的触发
                if (isNeedRelayout) {
                    widgetGroup.notifyInfoChange()
                    widgetGroup.requestLayout()
                }
                statusListener?.invoke(LoadStatus.SUCCESSFUL)
            }
        }
    }

    /**
     * 保存当前数据到数据库中
     */
    fun saveToDB(statusListener: ((status: LoadStatus) -> Unit)? = null) {
        doAsync({ uiThread { statusListener?.invoke(LoadStatus.ERROR) } }) {
            // 由于是数据操作，这里进行加锁保护
            synchronized("saveToDB-LOCK") {
                uiThread { statusListener?.invoke(LoadStatus.START) }
                // 新的面板的集合
                val newList = ArrayList<PanelInfo>()
                // 被删除的集合
                val deletedList = ArrayList<PanelInfo>()
                // 需要更新的集合
                val updateList = ArrayList<PanelInfo>()
                // 老的面板集合
                val oldPanelList = ArrayList<PanelInfo>()
                // 新的面板集合
                val newPanelList = ArrayList<Panel<*>>()
                newPanelList.addAll(panelList)
                // 从数据库得到现存的面板描述信息
                val db = DatabaseHelper.write(context).getOnePage(directionValue, hostId, oldPanelList)
                oldPanelList.forEach { oldInfo ->
                    // 从现存的集合中寻找
                    val panel = findPanelByInfo(newPanelList, oldInfo)
                    if (panel != null) {
                        // 如果存在，那么表示这是一个被保留下来的面板, 需要更新
                        // 并且从新面板集合中移除，表示已经检查过了
                        oldInfo.copy(panel.panelInfo)
                        updateList.add(oldInfo)
                        newPanelList.remove(panel)
                    } else {
                        // 如果不存在，那么表示这是一个被移除的面板，需要从数据库中移除
                        deletedList.add(oldInfo)
                    }
                }
                // 遍历后，得到需要更新的集合，需要删除的集合，
                // 而新面板集合剩下一部分没有被移除的，这些就是被新添加的
                newPanelList.forEach { panel ->
                    newList.add(panel.panelInfo)
                }

                //  对于已经检索出来的集合，进行数据库同步，
                //  开启数据库事务，来进行数据库更新
                db.transaction {
                    //先做删除
                    deletedList.forEach { info ->
                        delete(info.id)
                    }
                    // 再做添加
                    newList.forEach { info ->
                        install(info, directionValue, hostId)
                    }
                    // 最后做更新
                    updateList.forEach { info ->
                        update(info, directionValue, hostId)
                    }
                }
                uiThread {
                    // 至此，完成了一次数据更新并存储的服务
                    statusListener?.invoke(LoadStatus.SUCCESSFUL)
                }
            }
        }
    }

    private fun findPanelByInfo(existedList: ArrayList<Panel<*>>, info: PanelInfo): Panel<*>? {
        if (info is SystemWidgetPanelInfo) {
            for (panel in existedList) {
                val panelInfo = panel.panelInfo
                if (panelInfo == info) {
                    return panel
                }
                if (panelInfo is SystemWidgetPanelInfo && panelInfo.appWidgetId == info.appWidgetId) {
                    return panel
                }
            }
            return null
        }
        for (panel in existedList) {
            if (panel.panelInfo == info) {
                return panel
            }
            if (info.id != PanelInfo.NO_ID && info.id == panel.panelInfo.id) {
                return panel
            }
        }
        return null
    }

    fun selectAppWidget() {
        appWidgetHelper.selectAppWidget()
    }

    fun onSelectWidgetError(lis: () -> Unit): WidgetHelper {
        appWidgetHelper.onSelectWidgetError(lis)
        return this
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return appWidgetHelper.onActivityResult(requestCode, resultCode, data) ||
                onPendingPanelResult(requestCode, resultCode, data)
    }

    fun onChildClick(lis: (Panel<*>) -> Boolean): WidgetHelper {
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
        pageIsStart = true
        registerLightSensor()
        postUpdate()
        appWidgetHelper.onStart()
        panelList.forEach {
            it.onPageStart()
        }
    }

    fun onStop() {
        pageIsStart = false
        unregisterLightSensor()
        handler.removeCallbacks(updateTask)
        appWidgetHelper.onStop()
        panelList.forEach {
            it.onPageStop()
        }
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
        val alpha = getAlphaByLight() * 1F / 255
        panelList.forEach {
            it.panelInfo.color = fg
            it.onColorChange(fg, alpha)
        }
        widgetGroup.setBackgroundColor(bg)
    }

    private fun Int.updateColorByLight(): Int {
        if (isAutoLight) {
            val alpha = getAlphaByLight()
            return this and 0xFFFFFF or (alpha shl 24)
        }
        return this
    }

    private fun getAlphaByLight(): Int {
        if (isAutoLight) {
            val alpha = (191 * lightValue / lightMaxValue).toInt() + 64
            return max(0, min(255, alpha))
        }
        return 255
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

    fun addPanel(info: PanelInfo, result: ((Panel<*>) -> Unit)? = null) {
        if (info.id == PanelInfo.NO_ID && info.initIntent != null && pendingPanel(info)) {
            return
        }
        val panel = panelAdapter.createPanelByInfo(info)
        panel.isInEditMode = isInEditMode
        addPanel(panel)
        panel.onInfoChange()
        result?.invoke(panel)
    }

    private fun addPanel(panel: Panel<*>) {
        panel.panelInfo.color = foregroundColor
        panelList.add(panel)
        widgetGroup.addPanel(panel)
        if (pageIsStart) {
            panel.onPageStart()
            panel.onUpdate()
        }
        onColorChange()
    }

    private fun pendingPanel(info: PanelInfo): Boolean {
        val requestId = generateRequestId()
        pendlingInfoList.append(requestId, info)
        if (activity is Activity) {
            activity.startActivityForResult(info.initIntent, requestId)
            return true
        }
        return false
    }

    private fun onPendingPanelResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val info = pendlingInfoList.get(requestCode)?:return false
        if (resultCode != Activity.RESULT_OK || data == null) {
            pendlingInfoList.remove(requestCode)
            return true
        }
        info.initData(data)
        info.initIntent = null
        addPanel(info)
        return true
    }

    private fun generateRequestId(): Int {
        if (requestCodeProgress < REQUEST_CODE_MARK) {
            requestCodeProgress++
            return requestCodeProgress and REQUEST_CODE_MARK or REQUEST_CODE_PREFIX
        }
        for (index in 1 .. REQUEST_CODE_MARK) {
            val id = index and REQUEST_CODE_MARK or REQUEST_CODE_PREFIX
            if (pendlingInfoList.get(id) == null) {
                return id
            }
        }
        throw RuntimeException("cant generate request id")
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

    fun postOnConfigurationChanged(newConfig: Configuration,
                                   run: (helper: WidgetHelper) -> Unit,
                                   delay: Long = 50) {
        widgetGroup.postDelayed({
            val type = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
            if (isPortrait != type) {
                isPortrait = type
                run(this)
            }
        }, delay)
    }

    enum class LoadStatus {
        SUCCESSFUL,
        ERROR,
        START
    }

}