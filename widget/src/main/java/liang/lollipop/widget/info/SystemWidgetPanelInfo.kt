package liang.lollipop.widget.info

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-08-05 13:25
 * 系统小部件的面板信息
 */
class SystemWidgetPanelInfo: PanelInfo() {

    companion object {
        private val EMPTY_WIDGET_PROVIDER_INFO = AppWidgetProviderInfo()
    }

    var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    var appWidgetProviderInfo: AppWidgetProviderInfo = EMPTY_WIDGET_PROVIDER_INFO

    val isEmpty: Boolean
        get() {
            return appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ||
                    appWidgetProviderInfo == EMPTY_WIDGET_PROVIDER_INFO
        }

}