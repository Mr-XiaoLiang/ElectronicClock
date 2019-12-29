package liang.lollipop.electronicclock.widget.panel

import android.content.Context
import android.view.View
import liang.lollipop.electronicclock.view.WheelTimerView
import liang.lollipop.electronicclock.widget.info.WheelTimerPanelInfo
import liang.lollipop.widget.widget.Panel

/**
 * @author lollipop
 * @date 2019-12-08 17:56
 */
class WheelTimerPanel(panelInfo: WheelTimerPanelInfo): Panel<WheelTimerPanelInfo>(panelInfo) {

    companion object {
        private val EMPTY_ARRAY = IntArray(0)
    }

    override fun createView(context: Context): View? {
        return WheelTimerView(context)
    }

    override fun onInfoChange() {
        super.onInfoChange()
        tryMyView<WheelTimerView> {
            it.setDayAValue(panelInfo.dayValues.valueA)
        }
    }

    private val ArrayList<Int>.valueA: Int
        get() {
            if (this.isEmpty()) {
                return 0
            }
            return this[0]
        }

}