package liang.lollipop.electronicclock.fragment

import android.view.View
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.widget.info.WheelTimerPanelInfo
import liang.lollipop.electronicclock.widget.panel.WheelTimerPanel
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2020-01-05 17:36
 * 滚轮时间的调整页面
 */
class WheelTimerAdjustmentFragment: PanelInfoAdjustmentFragment() {

    private val wheelTimerPanelInfo = WheelTimerPanelInfo()
    private val wheelTimerPanel = WheelTimerPanel(wheelTimerPanelInfo)

    override fun getPanelView(): View {
        return wheelTimerPanel.getView(context!!)
    }

    override fun getPanelInfo(): PanelInfo {
        return wheelTimerPanelInfo
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        info?.let { wheelTimerPanelInfo.copy(it) }
        putAdjustmentInfo()
        panelInitComplete()
    }

    private fun putAdjustmentInfo() {
        addAdjustmentInfo(
            multiple {
                key = WheelTimerPanelInfo.MONTH
                title = getString(R.string.title_wheel_month)
                summary = getString(R.string.summary_wheel_month)
                maxSize = 2
                add(getString(R.string.month_en_name), R.array.minute_en)
                add(getString(R.string.month_cn_name), R.array.minute_cn)
                add(getString(R.string.month_tr_name), R.array.minute_tr)
                for (id in wheelTimerPanelInfo.minuteValues) {
                    selected(id)
                }
            },
            multiple {
                key = WheelTimerPanelInfo.DAY
                title = getString(R.string.title_wheel_day)
                summary = getString(R.string.summary_wheel_day)
                maxSize = 2
                add(getString(R.string.day_of_month_en_name), R.array.day_of_month_en)
                add(getString(R.string.day_of_month_cn_name), R.array.day_of_month_cn)
                add(getString(R.string.day_of_month_tr_name), R.array.day_of_month_tr)
                for (id in wheelTimerPanelInfo.dayValues) {
                    selected(id)
                }
            },
            multiple {
                key = WheelTimerPanelInfo.WEEK
                title = getString(R.string.title_wheel_week)
                summary = getString(R.string.summary_wheel_week)
                maxSize = 2
                add(getString(R.string.day_of_week_en_name), R.array.day_of_week_en)
                add(getString(R.string.day_of_week_cn_name), R.array.day_of_week_cn)
                add(getString(R.string.day_of_week_tr_name), R.array.day_of_week_tr)
                add(getString(R.string.day_of_week_jp_name), R.array.day_of_week_jp)
                for (id in wheelTimerPanelInfo.weekValues) {
                    selected(id)
                }
            },
            multiple {
                key = WheelTimerPanelInfo.HOUR
                title = getString(R.string.title_wheel_hour)
                summary = getString(R.string.summary_wheel_hour)
                maxSize = 2
                add(getString(R.string.hour_en_name), R.array.hour_en)
                add(getString(R.string.hour_cn_name), R.array.hour_cn)
                add(getString(R.string.hour_tr_name), R.array.hour_tr)
                add(getString(R.string.hour_num_name), R.array.hour_num)
                for (id in wheelTimerPanelInfo.hourValues) {
                    selected(id)
                }
            },
            multiple {
                key = WheelTimerPanelInfo.MINUTE
                title = getString(R.string.title_wheel_minute)
                summary = getString(R.string.summary_wheel_minute)
                maxSize = 2
                add(getString(R.string.minute_en_name), R.array.minute_en)
                add(getString(R.string.minute_cn_name), R.array.minute_cn)
                add(getString(R.string.minute_tr_name), R.array.minute_tr)
                add(getString(R.string.minute_num_name), R.array.minute_num)
                for (id in wheelTimerPanelInfo.minuteValues) {
                    selected(id)
                }
            },
            multiple {
                key = WheelTimerPanelInfo.SECOND
                title = getString(R.string.title_wheel_second)
                summary = getString(R.string.summary_wheel_second)
                maxSize = 2
                add(getString(R.string.second_en_name), R.array.minute_en)
                add(getString(R.string.second_cn_name), R.array.minute_cn)
                add(getString(R.string.second_tr_name), R.array.minute_tr)
                add(getString(R.string.second_num_name), R.array.minute_num)
                for (id in wheelTimerPanelInfo.secondValues) {
                    selected(id)
                }
            },
            switch {
                key = WheelTimerPanelInfo.ANIMATION
                title = getString(R.string.title_is_animation)
                summaryOfTrue = getString(R.string.summary_animation_enable)
                summaryOfFalse = getString(R.string.summary_animation_disable)
                value = wheelTimerPanelInfo.animation
            }
        )
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
        when (info.key) {
            WheelTimerPanelInfo.ANIMATION -> {
                wheelTimerPanelInfo.animation = newValue.optBoolean(true)
            }
            WheelTimerPanelInfo.MONTH -> {
                newValue.tryPutId(wheelTimerPanelInfo.monthValues)
            }
            WheelTimerPanelInfo.DAY -> {
                newValue.tryPutId(wheelTimerPanelInfo.dayValues)
            }
            WheelTimerPanelInfo.WEEK -> {
                newValue.tryPutId(wheelTimerPanelInfo.weekValues)
            }
            WheelTimerPanelInfo.HOUR -> {
                newValue.tryPutId(wheelTimerPanelInfo.hourValues)
            }
            WheelTimerPanelInfo.MINUTE -> {
                newValue.tryPutId(wheelTimerPanelInfo.minuteValues)
            }
            WheelTimerPanelInfo.SECOND -> {
                newValue.tryPutId(wheelTimerPanelInfo.secondValues)
            }
        }
        wheelTimerPanel.onInfoChange()
    }

    private fun Any.tryPutId(list: ArrayList<Int>) {
        list.clear()
        if (this is ArrayList<*>) {
            for (id in this) {
                if (id is Int) {
                    list.add(id)
                }
            }
        }
    }

    override fun initInfoByValue(info: String) {
        wheelTimerPanelInfo.parse(JSONObject(info))
    }
}