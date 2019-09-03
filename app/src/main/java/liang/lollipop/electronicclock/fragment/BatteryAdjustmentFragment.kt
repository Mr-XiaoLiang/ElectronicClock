package liang.lollipop.electronicclock.fragment

import android.os.Bundle
import android.view.View
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

        panelInitComplete()
    }

    override fun getPanelInfo(): PanelInfo {
        return PanelInfo()
    }

    override fun onInfoChange(info: AdjustmentInfo<*>, newValue: Any) {
    }

}