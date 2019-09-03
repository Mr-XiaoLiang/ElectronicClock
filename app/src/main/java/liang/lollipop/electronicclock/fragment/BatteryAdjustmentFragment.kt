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
                summaryOfTrue = getString(R.string.summary_show_bg)
                summaryOfFalse = getString(R.string.summary_hide_bg)
                value = batteryInfo.isShowBg
            }
        )
    }

    override fun getPanelInfo(): PanelInfo {
        return batteryInfo
    }

    override fun onInfoChange(info: AdjustmentInfo<*>, newValue: Any) {
    }

}