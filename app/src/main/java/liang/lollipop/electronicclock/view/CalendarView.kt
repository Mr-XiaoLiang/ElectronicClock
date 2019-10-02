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
class CalendarView(context: Context, attrs: AttributeSet?, defStyleAttr:Int )
    : ViewGroup(context,attrs,defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    private var onSizeChangeListener: ((view: CalendarView,
                                        w: Int, h: Int, oldw: Int, oldh: Int) -> Unit)? = null

    /**
     * 参数设置
     */
    var options: Options = Options()
        set(value) {
            field = value
            notifyDataChange()
        }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChangeListener?.invoke(this, w, h, oldw, oldh)
    }

    fun notifyDataChange() {
        requestLayout()
        // 其他设置
    }

    private class DayView(context: Context) : View(context) {

        var element = LunarCalendar.Element()
            set(value) {
                field = value
                notifyDataChange()
            }

        val options = Options()

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
            // 最先是节气
            if (options.isShowSolarTerms && element.solarTerms.isNotEmpty()) {
                hasSecondary = true
                secondaryValue = element.solarTerms
            }
            // 其次是公元历的节日（常规情况下，公元历的节日比较让人重视
            if (options.isShowFestival && element.solarFestival.isNotEmpty()) {
                when {
                    // 如果已经有了，那么展示为小点
                    hasSecondary -> points.add(options.solarFestivalPointColor)
                    // 如果可以显示并且只有节日，那么占用它
                    element.solarFestival.size == 1 -> {
                        hasSecondary = true
                        secondaryValue = element.solarFestival[0]
                    }
                    // 否则的话，说明是没有被占用，并且是多个节日，
                    // 那么占用显示名额并且增加小点，表示有节日被省略
                    else -> {
                        hasSecondary = true
                        secondaryValue = element.solarFestival[0]
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
                        hasSecondary = true
                        secondaryValue = element.lunarFestival[0]
                    }
                    // 否则的话，说明是没有被占用，并且是多个节日，
                    // 那么占用显示名额并且增加小点，表示有节日被省略
                    else -> {
                        hasSecondary = true
                        secondaryValue = element.lunarFestival[0]
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
                    canvas.rotate(angle * -1)
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

    enum class Type {
        Month, Week, Day
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
        var mainFontSizeWeight = 0.24F
        /** 次要文字尺寸比例 **/
        var secondaryFontSizeWeight = 0.16F
        /** 标识点的尺寸比例 **/
        var identificationSizeWeight = 0.06F
        /** 最大的小点数量 **/
        var maxPointSize = 9

        fun copy(other: Options) {
            isShowWeek               = other.isShowWeek
            isShowLunar              = other.isShowLunar
            isShowFestival           = other.isShowFestival
            isShowSolarTerms         = other.isShowSolarTerms
            isShowAuspicious         = other.isShowAuspicious
            isOvalBg                 = other.isOvalBg
            todayTextColor           = other.todayTextColor
            todayBgColor             = other.todayBgColor
            otherTextColor           = other.otherTextColor
            otherBgColor             = other.otherBgColor
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

}