package liang.lollipop.electronicclock.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import liang.lollipop.base.dp2px
import liang.lollipop.base.lazyBind
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.LunarAuspiciousDayInfo
import liang.lollipop.electronicclock.bean.LunarFestivalInfo
import liang.lollipop.electronicclock.databinding.ActivityLunarBinding
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

    private val binding: ActivityLunarBinding by lazyBind()

    private var isPortrait = true

    companion object {
        private const val ARG_TIME = "ARG_TIME"

        private const val FIRST_SHOW = "LUNAR_ACTIVITY_FIRST_SHOW"

        fun startByTime(context: Context, time: Long) {
            context.startActivity(Intent(context, LunarActivity::class.java).apply {
                putExtra(ARG_TIME, time)
            })
        }
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

    override fun createContentView(): View {
        return binding.root.apply {
            layoutParams = ViewGroup.LayoutParams(240.dp2px, 480.dp2px)
        }
    }

    private fun initView() {
        if (isPortrait) {
            binding.bodyScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                    binding.headerInfoGroup.alpha =
                        1 - (scrollY * 1F / binding.headerInfoGroup.height)
                }
            )
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initData() {
        val time = intent.getLongExtra(ARG_TIME, System.currentTimeMillis())
        val calendar = LunarCalendar.getCalendar(time)
        // 节气显示
        if (TextUtils.isEmpty(calendar.solarTerms)) {
            binding.cnDayView.text = calendar.lDayChinese
            binding.cnDayView2.visibility = View.GONE
        } else {
            binding.cnDayView.text = calendar.solarTerms
            binding.cnDayView2.text = calendar.lDayChinese
            binding.cnDayView2.visibility = View.VISIBLE
        }
        // 月份显示
        binding.cnMonthView.text = calendar.lMonthChinese
        // 闰月显示
        binding.runView.visibility = if (calendar.isLeap) {
            View.VISIBLE
        } else {
            View.GONE
        }
        // 公元历显示
        binding.dateView.text = "${calendar.sYear}-${calendar.sMonth}-${calendar.sDay}"
        // 显示八字
        binding.cnCharacterView.text =
            "${calendar.cYear}(${calendar.animals})年 ${calendar.cMonth}月 ${calendar.cDay}日"
        // 显示节假日
        val festivalData = ArrayList<LunarFestivalInfo>()
        for (f in calendar.solarFestival) {
            festivalData.add(LunarFestivalInfo(f))
        }
        for (f in calendar.lunarFestival) {
            festivalData.add(LunarFestivalInfo(f, LunarFestivalInfo.TYPE_LUNAR))
        }
        if (festivalData.isEmpty()) {
            binding.festivalCard.visibility = View.GONE
        } else {
            binding.festivalCard.visibility = View.VISIBLE
            binding.festivalList.layoutManager = FlexboxLayoutManager(this)
            binding.festivalList.adapter = LunarFestivalAdapter(festivalData, layoutInflater)
            binding.festivalList.adapter?.notifyDataSetChanged()
        }
        // 黄历显示
        val auspiciousDay = calendar.auspiciousDay
        binding.auspiciousKeyView.text = auspiciousDay.key
        binding.auspiciousDetailView.text = auspiciousDay.detail

        binding.auspiciousIconView.visibility = if (auspiciousDay.type > 1) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val matterData = ArrayList<LunarAuspiciousDayInfo>()
        for (name in auspiciousDay.matter) {
            matterData.add(LunarAuspiciousDayInfo(name))
        }
        binding.auspiciousList.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.auspiciousList.adapter = LunarAuspiciousDayAdapter(matterData, layoutInflater)

        val tabooData = ArrayList<LunarAuspiciousDayInfo>()
        for (name in auspiciousDay.taboo) {
            tabooData.add(LunarAuspiciousDayInfo(name))
        }
        binding.fierceList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.fierceList.adapter = LunarAuspiciousDayAdapter(tabooData, layoutInflater)

        // 星宿展示
        val cnStar = calendar.cnStar
        binding.cnStarKeyView.text = cnStar.key
        binding.cnStarGroupView.text = cnStar.group
        binding.cnStarKindView.text = cnStar.kind
        binding.cnStarInfoView.text = cnStar.detail
        val cnStarInscriptionBuilder = StringBuilder()
        for (index in cnStar.inscription.indices) {
            if (index != 0) {
                cnStarInscriptionBuilder.append("\n")
            }
            cnStarInscriptionBuilder.append(cnStar.inscription[index])
        }
        binding.cnStarInscriptionView.text = cnStarInscriptionBuilder.toString()
    }

}
