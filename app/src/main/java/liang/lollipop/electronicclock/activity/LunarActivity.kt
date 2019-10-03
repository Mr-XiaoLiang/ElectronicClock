package liang.lollipop.electronicclock.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.activity_lunar.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.LunarAuspiciousDayInfo
import liang.lollipop.electronicclock.bean.LunarFestivalInfo
import liang.lollipop.electronicclock.list.LunarAuspiciousDayAdapter
import liang.lollipop.electronicclock.list.LunarFestivalAdapter
import liang.lollipop.electronicclock.utils.LunarCalendar
import liang.lollipop.electronicclock.utils.getPreferences
import liang.lollipop.electronicclock.utils.putPreferences

/**
 * 黄历，农历的Activity
 * @author Lollipop
 */
class LunarActivity : DialogActivity() {

    override val contentViewId: Int
        get() = R.layout.activity_lunar

    private var isPortrait = true

//    private val logger = Utils.loggerI("LunarActivity")

    companion object {
        private const val ARG_TIME = "ARG_TIME"

        private const val FIRST_SHOW = "LUNAR_ACTIVITY_FIRST_SHOW"

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
        if (getPreferences(FIRST_SHOW, true)) {
            alert {
                setMessage(R.string.lunar_hint)
                setPositiveButton(R.string.enter) { dialog, _ ->
                    putPreferences(FIRST_SHOW, false)
                    dialog.dismiss()
                }
                show()
            }
        }
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
        // 显示八字
        cnCharacterView.text = "${calendar.cYear}(${calendar.animals})年 ${calendar.cMonth}月 ${calendar.cDay}日"
        // 显示节假日
        val festivalData = ArrayList<LunarFestivalInfo>()
        for (f in calendar.solarFestival) {
            festivalData.add(LunarFestivalInfo(f))
        }
        for (f in calendar.lunarFestival) {
            festivalData.add(LunarFestivalInfo(f, LunarFestivalInfo.TYPE_LUNAR))
        }
        if (festivalData.isEmpty()) {
            festivalCard.visibility = View.GONE
        } else {
            festivalCard.visibility = View.VISIBLE
            festivalList.layoutManager = FlexboxLayoutManager(this)
            festivalList.adapter = LunarFestivalAdapter(festivalData, layoutInflater)
            festivalList.adapter?.notifyDataSetChanged()
        }
        // 黄历显示
        val auspiciousDay = calendar.auspiciousDay
        auspiciousKeyView.text = auspiciousDay.key
        auspiciousDetailView.text = auspiciousDay.detail

        auspiciousIconView.visibility = if (auspiciousDay.type > 1) { View.VISIBLE } else { View.GONE }

        val matterData = ArrayList<LunarAuspiciousDayInfo>()
        for (name in auspiciousDay.matter) {
            matterData.add(LunarAuspiciousDayInfo(name))
        }
        auspiciousList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        auspiciousList.adapter = LunarAuspiciousDayAdapter(matterData, layoutInflater)

        val tabooData = ArrayList<LunarAuspiciousDayInfo>()
        for (name in auspiciousDay.taboo) {
            tabooData.add(LunarAuspiciousDayInfo(name))
        }
        fierceList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        fierceList.adapter = LunarAuspiciousDayAdapter(tabooData, layoutInflater)

        // 星宿展示
        val cnStar = calendar.cnStar
        cnStarKeyView.text = cnStar.key
        cnStarGroupView.text = cnStar.group
        cnStarKindView.text = cnStar.kind
        cnStarInfoView.text = cnStar.detail
        val cnStarInscriptionBuilder = StringBuilder()
        for (index in cnStar.inscription.indices) {
            if (index != 0) {
                cnStarInscriptionBuilder.append("\n")
            }
            cnStarInscriptionBuilder.append(cnStar.inscription[index])
        }
        cnStarInscriptionView.text = cnStarInscriptionBuilder.toString()
    }

}
