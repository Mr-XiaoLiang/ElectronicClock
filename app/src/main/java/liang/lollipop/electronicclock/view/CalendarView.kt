package liang.lollipop.electronicclock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import liang.lollipop.electronicclock.utils.LunarCalendar
import kotlin.math.min


/**
 * @author lollipop
 * @date 2019-10-01 23:42
 * 日历的View
 */
class CalendarView(context: Context, attrs: AttributeSet?, defStyleAttr:Int)
    : ViewGroup(context,attrs,defStyleAttr), WeightPaddingView {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    private var onSizeChangeListener: ((view: CalendarView,
                                        w: Int, h: Int, oldw: Int, oldh: Int) -> Unit)? = null

    private var year = 0
    /**
     * month = [0 ~ 11]
     */
    private var month = 0

    /**
     * 农历的日历
     */
    private var lunarCalendar: LunarCalendar? = null

    /**
     * 周的数量
     */
    private var weekSize = 0

    /**
     * 星期的view的高度
     */
    private var weekViewHeight = 0.6F

    /**
     * 每天的View
     */
    private val dayViewList = ArrayList<DayView>()

    /**
     * 星期的View
     */
    private val weekViewList = ArrayList<NumberPointIcon>()

    /**
     * 空的日期信息
     */
    private val emptyElement = LunarCalendar.Element()

    /**
     * 当某天被点击时
     */
    private var onDayViewClickListener: OnDayViewClickListener? = null

    private val childClickListener = OnClickListener {
        if (it is DayView) {
            val element = it.element
            onDayViewClickListener?.onDayViewClick(element.sYear, element.sMonth, element.sDay)
        }
    }

    /**
     * 日历展示模式
     */
    var calendarType = Type.Month
        set(value) {
            field = value
            notifyDataChange()
        }

    /**
     * 参数设置
     */
    val options = Options()

    fun changeOptions(option: Options, isCopyTextColor: Boolean = true) {
        options.copy(option, isCopyTextColor)
        notifyDataChange()
    }

    fun onDayViewClick(listener: OnDayViewClickListener) {
        onDayViewClickListener = listener
    }

    fun onDayViewClick(listener: (year: Int, month: Int, day: Int) -> Unit) {
        onDayViewClick(object : OnDayViewClickListener {
            override fun onDayViewClick(year: Int, month: Int, day: Int) {
                listener(year, month, day)
            }
        })
    }

    private val weightPaddingViewHelper = WeightPaddingViewHelper()

    override val paddingLeftW: Float
        get() {
            return weightPaddingViewHelper.paddingLeftW(width)
        }
    override val paddingTopW: Float
        get() {
            return weightPaddingViewHelper.paddingTopW(height)
        }
    override val paddingRightW: Float
        get() {
            return weightPaddingViewHelper.paddingRightW(width)
        }
    override val paddingBottomW: Float
        get() {
            return weightPaddingViewHelper.paddingBottomW(height)
        }

    init {
        LunarCalendar.getMonth(System.currentTimeMillis()) { year, month ->
            dateChange(year, month)
        }
    }

    override fun setWeightPadding(left: Float, top: Float, right: Float, bottom: Float) {
        weightPaddingViewHelper.setWeightPadding(left, top, right, bottom)
        requestLayout()
    }

    /**
     * 指定日历显示的时间
     */
    fun dateChange(year: Int, month: Int) {
        this.year = year
        this.month = month % 12
        lunarCalendar = LunarCalendar.getCalendar(year, month)
        onDataChange()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (lunarCalendar == null || weekSize < 1) {
            // 如果没有有效的数据，那么隐藏所有内容
            for (i in 0 until childCount) {
                getChildAt(i).visibility = View.GONE
            }
            return
        }
        dayViewList.forEach {
            it.isActive = false
        }
        // 排版
        val left = paddingLeftW
        val top = paddingTopW
        val width = r - l - paddingLeftW - paddingRightW
        val height = b - t - paddingTopW - paddingBottomW
        when (calendarType) {
            // 天模式下，只展示当天的信息，同时占满整个View
            Type.Day -> {
                val day = dayViewList[0]
                day.element = lunarCalendar?.today?:emptyElement
                layoutDayView(day, left.toInt(), top.toInt(),
                    (left + width).toInt(), (top + height).toInt())
            }
            // 星期模式，只展示一周的内容
            Type.Week -> {
                layoutByWeekType(left, top, width, height)
            }
            // 月模式，展示整月的数据
            Type.Month -> {
                layoutByMonthType(left, top, width, height)
            }
        }
        dayViewList.forEach {
            if (!it.isActive) {
                it.visibility = View.GONE
            }
        }
    }

    private fun layoutByWeekType(left: Float, top: Float, width: Float, height: Float) {
        val calendar = lunarCalendar?:return
        // 每一天的View的尺寸
        val dayWidth = width / 7F
        val dayHeight = if (options.isShowWeek) {
            height / (1 + weekViewHeight)
        } else {
            height * 1F
        }
        // 对星期进行排版
        val weekHeight = layoutWeek(left, top, dayWidth, dayHeight)
        // 对天进行排版
        val elementArray = calendar.elementArray
        val weekOffset = if (options.isStartingOnSunday) { 0 } else { 6 }
        // 如果今天的不存在的，那么随便拿第一周就好了
        val todayElement = if (calendar.today != null) {
            elementArray.indexOf(calendar.today)
        } else {
            0
        }
        // 第几个星期
        val weekIndex = (todayElement + calendar.firstWeek + weekOffset) / 7
        // 首先拿到开始的index， 如果是第一周，那么直接拿第一天
        val startIndex: Int
        val startWeek: Int
        if (weekIndex == 0) {
            startIndex = 0
            startWeek = (calendar.firstWeek + weekOffset) % 7
        } else {
            startIndex = todayElement - (todayElement + calendar.firstWeek + weekOffset) % 7
            startWeek = 0
        }
        var x = left + dayWidth * startWeek
        val y = weekHeight + top
        for (index in 0 until (7 - startWeek)) {
            if (startIndex + index >= dayViewList.size) {
                break
            }
            val day = dayViewList[index]
            day.element = elementArray[startIndex + index]
            layoutDayView(day, x.toInt(), y.toInt(),
                (x + dayWidth).toInt(), (y + dayHeight).toInt())
            x += dayWidth
        }
    }

    private fun layoutByMonthType(left: Float, top: Float, width: Float, height: Float) {
        val calendar = lunarCalendar?:return
        // 每一天的View的尺寸
        val dayWidth = width / 7F
        val dayHeight = if (options.isShowWeek) {
            height / (weekSize + weekViewHeight)
        } else {
            height * 1F / weekSize
        }
        // 对星期进行排版
        val weekHeight = layoutWeek(left, top, dayWidth, dayHeight)
        // 对天进行排版
        // 首先拿到开始的index
        val startIndex = if (options.isStartingOnSunday) {
            calendar.firstWeek
        } else {
            // 为了防止出现负数，做一点处理
            (calendar.firstWeek + 7 - 1) % 7
        }
        val endIndex = if (options.isStartingOnSunday) { 6 }  else { 0 }
        // 直接开始排版
        val elementArray = calendar.elementArray
        var x = left + dayWidth * startIndex
        var y = weekHeight + top
        for (index in elementArray.indices) {
            val day = dayViewList[index]
            day.visibility = View.VISIBLE
            day.element = elementArray[index]
            layoutDayView(day, x.toInt(), y.toInt(),
                (x + dayWidth).toInt(), (y + dayHeight).toInt())
            if ((index + startIndex) % 7 == endIndex) {
                x = left
                y += dayHeight
            } else {
                x += dayWidth
            }
        }
    }

    private fun layoutWeek(left: Float, top: Float, dayWidth: Float, dayHeight: Float): Float {
        // 如果显示星期的话，那么开始排版星期
        if (options.isShowWeek) {
            // 星期的View的尺寸
            val weekHeight = dayHeight * weekViewHeight
            var x = left
            var weekNumber = if (options.isStartingOnSunday) { 0 } else { 1 }
            val padding = (min(weekHeight, dayWidth) * 0.3F).toInt()
            // 遍历每一个星期的View
            for (week in weekViewList) {
                week.color = options.otherTextColor
                week.visibility = View.VISIBLE
                // 为他设置显示的数字
                week.number = weekNumber % 7
                week.setPadding(padding, padding, padding, padding)
                // 按顺序排版
                week.layout(x.toInt(), top.toInt(),
                    (x + dayWidth).toInt(), (top + weekHeight).toInt())
                // 增加计数
                weekNumber ++
                // 横轴偏移
                x += dayWidth
            }
            return weekHeight
        } else {
            for (week in weekViewList) {
                week.visibility = View.GONE
            }
        }
        return 0F
    }

    private fun layoutDayView(day: DayView, left: Int, top: Int, right: Int, bottom: Int) {
        val padding = (min(right - left, bottom - top) * 0.06F).toInt()
        day.setPadding(padding, padding, padding, padding)
        day.layout(left, top, right, bottom)
        day.isActive = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChangeListener?.invoke(this, w, h, oldw, oldh)
    }

    /**
     * 触发一次数据检查
     */
    fun notifyDataChange() {
        onDataChange()
        invalidate()
    }

    override fun invalidate() {
        super.invalidate()
        for (index in 0 until childCount) {
            getChildAt(index).invalidate()
        }
    }

    /**
     * 计算日历相关数据
     */
    private fun onDataChange() {
        val calendar = lunarCalendar?:return
        // 第一天星期几[0~6]，周日为0
        var firstWeek = calendar.firstWeek
        // 如果不是周日开始，那么开始的星期延后一天
        if (!options.isStartingOnSunday) {
            firstWeek -= 1
        }
        val daySize = calendar.elementArray.size + firstWeek
        weekSize = daySize / 7
        // 如果不是完整的周，那么再加一周
        if (daySize % 7 != 0) {
            weekSize++
        }
        // 检查一次星期的View
        if (options.isShowWeek && weekViewList.size < 7) {
            while (weekViewList.size < 7) {
                val view = NumberPointIcon(context)
                weekViewList.add(view)
                addView(view)
            }
        }
        // 检查天的View，并且补齐
        val elementSize = when (calendarType) {
            Type.Month -> calendar.elementArray.size
            Type.Week -> 7
            Type.Day -> 1
        }
        while (dayViewList.size < elementSize) {
            val view = DayView(context)
            view.setOnClickListener(childClickListener)
            dayViewList.add(view)
            addView(view)
        }
        // 为新旧view都更新一次参数
        dayViewList.forEach {
            it.options.copy(options)
        }
        weekViewList.forEach {
            it.color = options.otherTextColor
        }
        if (calendarType == Type.Day || !options.isShowWeek) {
            for (week in weekViewList) {
                if (week.visibility != View.GONE) {
                    week.visibility = View.GONE
                }
            }
        } else {
            for (week in weekViewList) {
                if (week.visibility != View.VISIBLE) {
                    week.visibility = View.VISIBLE
                }
            }
        }
    }

    private class DayView(context: Context) : View(context) {

        var element = LunarCalendar.Element()
            set(value) {
                field = value
                notifyDataChange()
            }

        val options = Options()

        var isActive = false

        private val bounds = RectF()

        /**
         * 显示的小点们
         * 内容是颜色
         */
        private val points = ArrayList<Int>()

        /**
         * 主要内容
         */
        private var mainValue = ""

        /**
         * 次要内容
         */
        private var secondaryValue = ""

        /**
         * 最大的文字长度
         */
        private val maxSecondaryLength = 3

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.CENTER
        }

        fun notifyDataChange() {
            // 清空小点
            points.clear()
            // 设置主文字
            mainValue = "${element.sDay}"
            // 按照优先级确认次文字是否已经占用
            var hasSecondary = false
            secondaryValue = ""
            // 最先是节气
            if (options.isShowSolarTerms && element.solarTerms.isNotEmpty()) {
                hasSecondary = true
                secondaryValue = element.solarTerms
            }
            // 如果是月初，那么优先展示月份
            if (element.lDay == 1 && options.isShowLunar) {
                hasSecondary = true
                secondaryValue = "${element.lMonthChinese}月"
            }
            // 其次是公元历的节日（常规情况下，公元历的节日比较让人重视
            if (options.isShowFestival && element.solarFestival.isNotEmpty()) {
                when {
                    // 如果已经有了，那么展示为小点
                    hasSecondary -> points.add(options.solarFestivalPointColor)
                    // 如果可以显示并且只有节日，那么占用它
                    element.solarFestival.size == 1 -> {
                        val value = element.solarFestival[0]
                        if (value.length > maxSecondaryLength) {
                            points.add(options.solarFestivalPointColor)
                        } else {
                            hasSecondary = true
                            secondaryValue = value
                        }
                    }
                    // 否则的话，说明是没有被占用，并且是多个节日，
                    // 那么占用显示名额并且增加小点，表示有节日被省略
                    else -> {
                        val values = element.solarFestival.filter { it.length <= maxSecondaryLength }
                        if (values.isNotEmpty()) {
                            hasSecondary = true
                            secondaryValue = values[0]
                        }
                        points.add(options.solarFestivalPointColor)
                    }
                }
            }
            // 农历的假日
            if (options.isShowFestival && element.lunarFestival.isNotEmpty()) {
                when {
                    // 如果已经有了，那么展示为小点
                    hasSecondary -> points.add(options.lunarFestivalPointColor)
                    // 如果可以显示并且只有节日，那么占用它
                    element.lunarFestival.size == 1 -> {
                        val value = element.lunarFestival[0]
                        if (value.length > maxSecondaryLength) {
                            points.add(options.lunarFestivalPointColor)
                        } else {
                            hasSecondary = true
                            secondaryValue = value
                        }
                    }
                    // 否则的话，说明是没有被占用，并且是多个节日，
                    // 那么占用显示名额并且增加小点，表示有节日被省略
                    else -> {
                        val values = element.lunarFestival.filter { it.length <= maxSecondaryLength }
                        if (values.isNotEmpty()) {
                            hasSecondary = true
                            secondaryValue = values[0]
                        }
                        points.add(options.lunarFestivalPointColor)
                    }
                }
            }
            // 如果没有被占用，那么显示为农历
            if (!hasSecondary && options.isShowLunar) {
                secondaryValue = element.lDayChinese
            }
            // 如果是吉日，那么展示吉日的小点
            if (element.isAuspiciousDay && options.isShowAuspicious) {
                points.add(options.auspiciousPointColor)
            }
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            canvas?:return
            // 无效的数据，认为业务不希望我们绘制
            if (element.sYear == 0 || element.sMonth == 0 || element.sDay == 0) {
                return
            }
            val radius = min(bounds.width(), bounds.height()) / 2
            // 背景绘制
            paint.color = if (element.isToday) { options.todayBgColor } else { options.otherBgColor }
            if (options.isOvalBg) {
                canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, paint)
            } else {
                canvas.drawRect(bounds, paint)
            }

            // 绘制小点
            if (points.isNotEmpty()) {
                // 小点半径
                val pointRadius = radius * options.identificationSizeWeight
                // 小点的间隔
                val angleStep = 360F / options.maxPointSize
                // 为了保证小点总是整体位于下方居中，需要调整开始的偏移角度
                val startAngle = 90F - ((points.size - 1) * angleStep / 2)
                var angle = startAngle
                // 为了方便绘制小点，需要将坐标系起点移动到画布中心
                canvas.save()
                canvas.translate(bounds.centerX(), bounds.centerY())
                for (color in points) {
                    paint.color = color
                    // 旋转相应的角度然后绘制
                    canvas.save()
                    canvas.rotate(angle)
                    canvas.drawCircle(radius - pointRadius, 0F, pointRadius, paint)
                    canvas.restore()
                    angle += angleStep
                }
                canvas.restore()
            }
            // 绘制文字
            // 计算主要文字尺寸，由于是半径，需要 * 2
            val mainFontSize = radius * options.mainFontSizeWeight * 2
            // 其次计算次要文字的尺寸，如果文字为空，那么就放弃计算了
            val secondaryFontSize = if (secondaryValue.isNotEmpty()) {
                radius * options.secondaryFontSizeWeight * 2
            } else {
                0F
            }
            // 绘制主要文字
            val mainFontY = (-1 * secondaryFontSize) / 2 + bounds.centerY()
            paint.color = if (element.isToday) {
                options.todayTextColor } else { options.otherTextColor }
            paint.textSize = mainFontSize
            val fm = paint.fontMetrics
            var textY = mainFontY - fm.descent + (fm.descent - fm.ascent) / 2
            canvas.drawText(mainValue, bounds.centerX(), textY, paint)

            // 绘制次要文字(在有内容的情况下）
            if (secondaryValue.isNotEmpty()) {
                val secondaryFontY = (mainFontSize) / 2 + bounds.centerY()
                paint.textSize = secondaryFontSize
                // 设置字体大小后，再次更新字体尺寸计算工具
                paint.getFontMetrics(fm)
                textY = secondaryFontY - fm.descent + (fm.descent - fm.ascent) / 2
                canvas.drawText(secondaryValue, bounds.centerX(), textY, paint)
            }
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            bounds.set(paddingLeft.toFloat(),
                paddingTop.toFloat(),
                (right - left) - paddingRight.toFloat(),
                (bottom - top) - paddingBottom.toFloat())
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            bounds.set(paddingLeft.toFloat(),
                paddingTop.toFloat(),
                w - paddingRight.toFloat(),
                h - paddingBottom.toFloat())
        }

    }

    enum class Type(val value: Int) {
        Month(0),
        Week(1),
        Day(2)
    }

    open class Options {
        /** 星期 **/
        var isShowWeek = true
        /** 农历 **/
        var isShowLunar = true
        /** 节日 **/
        var isShowFestival = true
        /** 节气 **/
        var isShowSolarTerms = true
        /** 吉日 **/
        var isShowAuspicious = true
        /** 是否圆形背景 **/
        var isOvalBg = true
        /** 今天的文字颜色 **/
        var todayTextColor = Color.WHITE
        /** 今天的背景颜色 **/
        var todayBgColor = Color.BLUE
        /** 其他的文字颜色 **/
        var otherTextColor = Color.BLACK
        /** 其他的背景颜色 **/
        var otherBgColor = Color.TRANSPARENT
        /** 周日开始 **/
        var isStartingOnSunday = true
        /** 是否显示日程 **/
        var isShowSchedule = true
        /** 公元节日小点的颜色 **/
        var solarFestivalPointColor = Color.BLUE
        /** 农历节日小点的颜色 **/
        var lunarFestivalPointColor = Color.CYAN
        /** 吉日的小点颜色 **/
        var auspiciousPointColor = Color.RED

        /** 主要文字尺寸比例 **/
        var mainFontSizeWeight = 0.5F
        /** 次要文字尺寸比例 **/
        var secondaryFontSizeWeight = 0.16F
        /** 标识点的尺寸比例 **/
        var identificationSizeWeight = 0.1F
        /** 最大的小点数量 **/
        var maxPointSize = 9

        fun copy(other: Options, isCopyTextColor: Boolean = true) {
            isShowWeek               = other.isShowWeek
            isShowLunar              = other.isShowLunar
            isShowFestival           = other.isShowFestival
            isShowSolarTerms         = other.isShowSolarTerms
            isShowAuspicious         = other.isShowAuspicious
            isOvalBg                 = other.isOvalBg
            if (isCopyTextColor) {
                todayTextColor           = other.todayTextColor
                todayBgColor             = other.todayBgColor
                otherTextColor           = other.otherTextColor
                otherBgColor             = other.otherBgColor
            }
            isStartingOnSunday       = other.isStartingOnSunday
            isShowSchedule           = other.isShowSchedule
            solarFestivalPointColor  = other.solarFestivalPointColor
            lunarFestivalPointColor  = other.lunarFestivalPointColor
            auspiciousPointColor     = other.auspiciousPointColor
            mainFontSizeWeight       = other.mainFontSizeWeight
            secondaryFontSizeWeight  = other.secondaryFontSizeWeight
            identificationSizeWeight = other.identificationSizeWeight
            maxPointSize             = other.maxPointSize
        }

    }

    interface OnDayViewClickListener {
        fun onDayViewClick(year: Int, month: Int, day: Int)
    }

}