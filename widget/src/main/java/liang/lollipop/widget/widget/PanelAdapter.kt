package liang.lollipop.widget.widget

import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.info.SystemWidgetPanelInfo
import liang.lollipop.widget.panel.EmptyPanel
import liang.lollipop.widget.panel.SystemWidgetPanel
import liang.lollipop.widget.utils.AppWidgetHelper
import liang.lollipop.widget.utils.PanelProviders

/**
 * @author lollipop
 * @date 2019-08-03 19:23
 * 面板的适配器
 */
class PanelAdapter(private val appWidgetHelper: AppWidgetHelper) {

    companion object {

        var panelProviders: PanelProviders? = null

        fun className(info: PanelInfo): String {
            return info.javaClass.name
        }

        fun newInfo(name: String): PanelInfo {
            return when (name) {
                ClockPanelInfo::class.java.name -> ClockPanelInfo()
                SystemWidgetPanelInfo::class.java.name -> SystemWidgetPanelInfo()
                else -> {
                    panelProviders?.createInfoByName(name)?:PanelInfo()
                }
            }
        }
    }

    private var onErrorListener: ((info: PanelInfo) -> Unit)? = null

    fun createPanelByInfo(info: PanelInfo): Panel<*> {
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
            else -> {
                panelProviders?.createPanelByInfo(info)?:EmptyPanel(info)
            }
        }
    }

    fun updateBySecond(panel: Panel<*>): Boolean {
        if (panel is SystemWidgetPanel || panel is EmptyPanel) {
            return false
        }
        return true
    }

    fun onError(lis: ((info: PanelInfo) -> Unit)? = null) {
        this.onErrorListener = lis
    }

}