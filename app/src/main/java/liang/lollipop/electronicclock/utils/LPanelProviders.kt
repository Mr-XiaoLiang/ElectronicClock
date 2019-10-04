package liang.lollipop.electronicclock.utils

import liang.lollipop.electronicclock.widget.info.BatteryPanelInfo
import liang.lollipop.electronicclock.widget.info.CalendarPanelInfo
import liang.lollipop.electronicclock.widget.panel.BatteryPanel
import liang.lollipop.electronicclock.widget.panel.CalendarPanel
import liang.lollipop.widget.utils.PanelProviders
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-10-05 00:52
 * 面板提供器
 */
class LPanelProviders: PanelProviders {
    override fun createPanelByInfo(info: PanelInfo): Panel<*>? {
        return when (info) {
            is BatteryPanelInfo -> BatteryPanel(info)
            is CalendarPanelInfo -> CalendarPanel(info)
            else -> null
        }
    }

    override fun createInfoByName(name: String): PanelInfo? {
        return when (name) {
            BatteryPanelInfo::class.java.name -> BatteryPanelInfo()
            CalendarPanelInfo::class.java.name -> CalendarPanelInfo()
            else -> null
        }
    }

}