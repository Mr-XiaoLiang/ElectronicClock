package liang.lollipop.electronicclock.fragment

import android.view.Gravity
import android.view.View
import android.widget.TextView
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.widget.widget.PanelInfo

/**
 * 空的调整面板
 * 用于无法找到有用的面板信息时，将会显示此面板
 */
class EmptyAdjustmentFragment: PanelInfoAdjustmentFragment() {

    companion object {
        fun getInstance(): EmptyAdjustmentFragment {
            return EmptyAdjustmentFragment()
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

    override fun onInfoChange(info: AdjustmentInfo<*>, newValue: Any) {
    }
}