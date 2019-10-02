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

        var element: LunarCalendar.Element = LunarCalendar.Element()

        val options = Options()

        private val bounds = RectF()

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
        }

        fun notifyDataChange() {
            requestLayout()
            // 其他设置
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
        /** 节日小点的颜色 **/
        var festivalPointColor = Color.BLUE
        /** 吉日的小点颜色 **/
        var auspiciousPointColor = Color.RED

        /** 主要文字尺寸比例 **/
        var mainFontSizeWeight = 0.24F
        /** 次要文字尺寸比例 **/
        var secondaryFontSizeWeight = 0.16F
        /** 标识点的尺寸比例 **/
        var identificationSizeWeight = 0.06F

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
            festivalPointColor       = other.festivalPointColor
            auspiciousPointColor     = other.auspiciousPointColor
            mainFontSizeWeight       = other.mainFontSizeWeight
            secondaryFontSizeWeight  = other.secondaryFontSizeWeight
            identificationSizeWeight = other.identificationSizeWeight
        }

    }

}