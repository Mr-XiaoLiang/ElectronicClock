package liang.lollipop.electronicclock.fragment

import android.view.View
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.widget.info.WheelTimerPanelInfo
import liang.lollipop.electronicclock.widget.panel.WheelTimerPanel
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2020-01-05 17:36
 * 滚轮时间的调整页面
 */
class WheelTimerAdjustmentFragment: PanelInfoAdjustmentFragment() {

    private val wheelTimerPanelInfo = WheelTimerPanelInfo()
    private val wheelTimerPanel = WheelTimerPanel(wheelTimerPanelInfo)

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPanelView(): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPanelInfo(): PanelInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initInfoByValue(info: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}