package liang.lollipop.widget.info

import android.graphics.Typeface
import liang.lollipop.widget.panel.ClockPanel
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-08-04 23:26
 * 时间面板的Info
 */
class ClockPanelInfo: PanelInfo() {

    fun createPanel(): ClockPanel {
        return ClockPanel(this)
    }

    val typeface: Typeface? = null

}