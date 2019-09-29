package liang.lollipop.electronicclock.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.widget.NestedScrollView
import kotlinx.android.synthetic.main.activity_lunar.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.LunarCalendar
import liang.lollipop.widget.utils.Utils

/**
 * 黄历，农历的Activity
 * @author Lollipop
 */
class LunarActivity : DialogActivity() {

    override val contentViewId: Int
        get() = R.layout.activity_lunar

    private var isPortrait = true

    private val logger = Utils.loggerI("LunarActivity")

    companion object {
        private const val ARG_TIME = "ARG_TIME"
    }

    override fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        super.onWindowInsetsChange(left, top, right, bottom)
        topBounds.setGuidelineBegin(top)
    }

    override fun filterRootGroupInset(left: Int, top: Int, right: Int, bottom: Int): IntArray {
        return intArrayOf(left, 0, right, bottom)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPortrait = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        initView()
        initData()
    }

    private fun initView() {
        if (isPortrait) {
            bodyScrollView.setOnScrollChangeListener {
                    _: NestedScrollView?,
                    _: Int, scrollY: Int,
                    _: Int, _: Int ->
                headerInfoGroup.alpha = 1 - (scrollY * 1F / headerInfoGroup.height)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        val time = intent.getLongExtra(ARG_TIME, System.currentTimeMillis())
        val calendar = LunarCalendar.getCalendar(time)
        // 节气显示
        if (TextUtils.isEmpty(calendar.solarTerms)) {
            cnDayView.text = calendar.lDayChinese
            cnDayView2.visibility = View.GONE
        } else {
            cnDayView.text = calendar.solarTerms
            cnDayView2.text = calendar.lDayChinese
            cnDayView2.visibility = View.VISIBLE
        }
        // 月份显示
        cnMonthView.text = calendar.lMonthChinese
        // 闰月显示
        runView.visibility = if (calendar.isLeap) { View.VISIBLE } else { View.GONE }
        // 公元历显示
        dateView.text = "${calendar.sYear}-${calendar.sMonth}-${calendar.sDay}"
        // 黄历显示
        val auspiciousDay = calendar.auspiciousDay
        auspiciousKeyView.text = auspiciousDay.key
        auspiciousDetailView.text = auspiciousDay.detail
    }

}
