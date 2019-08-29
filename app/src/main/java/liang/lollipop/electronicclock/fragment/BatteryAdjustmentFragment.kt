package liang.lollipop.electronicclock.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
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
                    putString(ARG_INFO_ID, "$id")
                }
            }
        }

    }

    override fun getPanelView(): View {
        return TextView(context).apply {
            gravity = Gravity.CENTER
            text = "?"
        }
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        panelInitComplete()
    }

    override fun getPanelInfo(): PanelInfo {
        return PanelInfo()
    }
}