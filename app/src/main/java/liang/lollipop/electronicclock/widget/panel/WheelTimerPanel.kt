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

    override fun createView(context: Context): View? {
        return WheelTimerView(context)
    }

    override fun onInfoChange() {
        super.onInfoChange()
        tryMyView<WheelTimerView> {
            it.setDayAValue(panelInfo.dayValues.valueA)
            it.setDayBValue(panelInfo.dayValues.valueB)
            it.setMonthAValue(panelInfo.monthValues.valueA)
            it.setMonthBValue(panelInfo.monthValues.valueB)
            it.setWeekAValue(panelInfo.weekValues.valueA)
            it.setWeekBValue(panelInfo.weekValues.valueB)
            it.setHourAValue(panelInfo.hourValues.valueA)
            it.setHourBValue(panelInfo.hourValues.valueB)
            it.setMinuteAValue(panelInfo.minuteValues.valueA)
            it.setMinuteBValue(panelInfo.minuteValues.valueB)
            it.setSecondAValue(panelInfo.secondValues.valueA)
            it.setSecondBValue(panelInfo.secondValues.valueB)
            it.simulation = panelInfo.simulation
            it.showGrid = panelInfo.showGrid
            if (!panelInfo.animation) {
                it.stop()
            } else if (isActive) {
                it.start()
            }
            it.notifyDataSetChange()
        }
    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        tryMyView<WheelTimerView> {
            it.color = color
            it.gridColor = color.changeAlpha((light * 88).toInt())
        }
    }

    override fun onPageStart() {
        super.onPageStart()
        if (panelInfo.animation) {
            tryMyView<WheelTimerView> {
                it.start()
            }
        }
    }

    override fun onPageStop() {
        super.onPageStop()
        tryMyView<WheelTimerView> {
            it.stop()
        }
    }

    private val ArrayList<Int>.valueA: Int
        get() {
            if (this.isEmpty()) {
                return 0
            }
            return this[0]
        }
    private val ArrayList<Int>.valueB: Int
        get() {
            if (this.size < 2) {
                return valueA
            }
            return this[1]
        }

}