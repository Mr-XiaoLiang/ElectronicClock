package liang.lollipop.electronicclock.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup

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

//    private var

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChangeListener?.invoke(this, w, h, oldw, oldh)
    }

    enum class Type {
        Month, Week, Day
    }

    class Options {
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

        /** 主要文字尺寸比例 **/
        var mainFontSizeWeight = 0.24F
        /** 次要文字尺寸比例 **/
        var secondaryFontSizeWeight = 0.16F
        /** 标识点的尺寸比例 **/
        var identificationSizeWeight = 0.06F
    }

}