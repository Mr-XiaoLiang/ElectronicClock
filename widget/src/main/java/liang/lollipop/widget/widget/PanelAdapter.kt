package liang.lollipop.widget.widget

import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.panel.EmptyPanel

/**
 * @author lollipop
 * @date 2019-08-03 19:23
 * 面板的适配器
 */
object PanelAdapter {

    fun <I: PanelInfo> createPanelByInfo(info: I): Panel<I> {
        return when (info) {
            is ClockPanelInfo -> info.createPanel()
            else -> EmptyPanel(info)
        } as Panel<I>
    }

    fun updateBySecond(panel: Panel<*>): Boolean {
        return true
    }

}