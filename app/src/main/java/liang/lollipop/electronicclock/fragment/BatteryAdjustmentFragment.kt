package liang.lollipop.electronicclock.fragment

import android.os.Bundle
import android.view.View
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.widget.info.BatteryPanelInfo
import liang.lollipop.electronicclock.widget.panel.BatteryPanel
import liang.lollipop.widget.widget.PanelInfo

/**
 * 电池的调整面板
 * 用于调整电池小部件的参数
 */
class BatteryAdjustmentFragment: PanelInfoAdjustmentFragment() {
    companion object {
        fun getInstance(id: Int): BatteryAdjustmentFragment {
            return BatteryAdjustmentFragment().apply {
                arguments = Bundle().apply {
                    if (id != PanelInfo.NO_ID) {
                        putString(ARG_INFO_ID, "$id")
                    }
                }
            }
        }
    }

    private val batteryInfo = BatteryPanelInfo()
    private val batteryPanel = BatteryPanel(batteryInfo)

    override fun getPanelView(): View {
        return batteryPanel.createView(context!!)
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        info?.let { batteryInfo.copy(it) }
        putAdjustmentInfo()
        panelInitComplete()
    }

    private fun putAdjustmentInfo() {
        addAdjustmentInfo(
            switch {
                key = BatteryPanelInfo.IS_SHOW_BG
                title = getString(R.string.title_show_bg)
                summaryOfTrue = getString(R.string.summary_show_bg)
                summaryOfFalse = getString(R.string.summary_hide_bg)
                value = batteryInfo.isShowBg
            },
            switch {
                key = BatteryPanelInfo.IS_SHOW_BORDER
                title = getString(R.string.title_show_border)
                summaryOfTrue = getString(R.string.summary_show_border)
                summaryOfFalse = getString(R.string.summary_hide_border)
                value = batteryInfo.isShowBorder
            },
            seekBar {
                key = BatteryPanelInfo.CORNER
                title = getString(R.string.title_corner)
                summary = getString(R.string.summary_corner)
                min = 0
                max = 100
                value = (batteryInfo.corner * 100).toInt()
            },
            colors {
                key = BatteryPanelInfo.COLOR_ARRAY
                title = getString(R.string.title_color_array)
                summary = getString(R.string.summary_color_array)
                maxSize = 100
                reset(batteryInfo.colorArray)
            },
            paddings {
                key = BatteryPanelInfo.PADDING
                title = getString(R.string.title_paddings)
                reset(batteryInfo.padding)
            },
            switch {
                key = BatteryPanelInfo.IS_ARC
                title = getString(R.string.title_arc_mode)
                summaryOfTrue = getString(R.string.summary_arc_true)
                summaryOfFalse = getString(R.string.summary_arc_false)
                value = batteryInfo.isArc
            },
            seekBar {
                key = BatteryPanelInfo.ARC_WIDTH
                relevantKey = BatteryPanelInfo.IS_ARC
                title = getString(R.string.title_arc_width)
                summary = getString(R.string.summary_arc_width)
                min = 0
                max = 100
                value = (batteryInfo.arcWidth * 100).toInt()
            },
            seekBar {
                key = BatteryPanelInfo.BORDER_WIDTH
                title = getString(R.string.title_border_width)
                summary = getString(R.string.summary_border_width)
                min = 0
                max = 100
                value = (batteryInfo.borderWidth * 100).toInt()
            },
            colors {
                key = BatteryPanelInfo.BORDER_COLOR
                title = getString(R.string.title_border_color)
                summary = getString(R.string.summary_border_color)
                reset(batteryInfo.borderColor)
            },
            switch {
                key = BatteryPanelInfo.IS_VERTICAL
                relevantKey = BatteryPanelInfo.IS_ARC
                relevantEnable = false
                title = getString(R.string.title_is_vertical)
                summaryOfTrue = getString(R.string.summary_vertical)
                summaryOfFalse = getString(R.string.summary_horizontal)
                value = batteryInfo.isVertical
            },
            switch {
                key = BatteryPanelInfo.IS_ANIMATION
                title = getString(R.string.title_is_animation)
                summaryOfTrue = getString(R.string.summary_animation_enable)
                summaryOfFalse = getString(R.string.summary_animation_disable)
                value = batteryInfo.isAnimation
            }
        )
    }

    override fun getPanelInfo(): PanelInfo {
        return batteryInfo
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
    }

}