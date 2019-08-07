package liang.lollipop.widget.widget

import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.info.SystemWidgetPanelInfo
import liang.lollipop.widget.panel.EmptyPanel
import liang.lollipop.widget.panel.SystemWidgetPanel
import liang.lollipop.widget.utils.AppWidgetHelper

/**
 * @author lollipop
 * @date 2019-08-03 19:23
 * 面板的适配器
 */
class PanelAdapter(private val appWidgetHelper: AppWidgetHelper) {

    private var onErrorListener: ((info: PanelInfo) -> Unit)? = null

    fun <I: PanelInfo> createPanelByInfo(info: I): Panel<I> {
        return when (info) {
            is ClockPanelInfo -> info.createPanel()
            is SystemWidgetPanelInfo -> {
                val view = appWidgetHelper.createViewByInfo(info)
                if (view != null) {
                    SystemWidgetPanel(info, view)
                } else {
                    onErrorListener?.invoke(info)
                    EmptyPanel(info)
                }
            }
            else -> EmptyPanel(info)
        } as Panel<I>
    }

    fun updateBySecond(panel: Panel<*>): Boolean {
        if (panel is SystemWidgetPanel || panel is EmptyPanel) {
            return false
        }
        return true
    }

    companion object {
        fun className(info: PanelInfo): String {
            return info.javaClass.name
        }

        fun newInfo(name: String): PanelInfo {
            return when (name) {
                ClockPanelInfo::class.java.name -> ClockPanelInfo()
                SystemWidgetPanelInfo::class.java.name -> SystemWidgetPanelInfo()
                else -> PanelInfo()
            }
        }
    }

}