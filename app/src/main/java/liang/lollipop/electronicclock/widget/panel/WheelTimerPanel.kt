package liang.lollipop.electronicclock.widget.panel

import liang.lollipop.electronicclock.drawable.WheelTimerDrawable
import liang.lollipop.electronicclock.widget.info.WheelTimerPanelInfo
import liang.lollipop.widget.widget.Panel

/**
 * @author lollipop
 * @date 2019-12-08 17:56
 */
class WheelTimerPanel(panelInfo: WheelTimerPanelInfo): Panel<WheelTimerPanelInfo>(panelInfo) {

    private val valueProvider = WheelTimerDrawable.ValueProvider()
    private val wheelTimerDrawable = WheelTimerDrawable(valueProvider)

}