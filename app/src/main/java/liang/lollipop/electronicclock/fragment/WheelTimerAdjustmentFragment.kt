package liang.lollipop.electronicclock.fragment

import android.view.View
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
        TODO("not implemented")
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initInfoByValue(info: String) {
        wheelTimerPanelInfo.parse(JSONObject(info))
    }
}