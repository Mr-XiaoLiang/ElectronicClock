package liang.lollipop.widget.panel

import android.appwidget.AppWidgetHostView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.widget.info.SystemWidgetPanelInfo
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.WidgetGroup

/**
 * @author lollipop
 * @date 2019-08-05 13:24
 * 系统小部件的面板
 */
class SystemWidgetPanel(info: SystemWidgetPanelInfo,
                        private val widgetView: AppWidgetHostView): Panel<SystemWidgetPanelInfo>(info) {

    override fun updatePanelInfo(group: WidgetGroup) {
        super.updatePanelInfo(group)
        val gridSize = group.gridSize
        if (gridSize.width <= 0 || gridSize.height <= 0) {
            return
        }
        // 如果是空的，那么直接将尺寸设置为0
        if (panelInfo.isEmpty) {
            panelInfo.sizeChange(0, 0)
            return
        }
        val providerInfo = panelInfo.appWidgetProviderInfo
        val minWidth = providerInfo.minWidth
        val minHeight = providerInfo.minHeight
        var spanX = minWidth / gridSize.width
        if (minWidth % gridSize.width != 0) {
            spanX++
        }
        if (spanX < 1) {
            spanX = 1
        }
        var spanY = minHeight / gridSize.height
        if (minHeight % gridSize.height != 0) {
            spanY++
        }
        if (spanY < 1) {
            spanY = 1
        }
        panelInfo.sizeChange(spanX, spanY)
    }

    override fun onUpdate() {
        super.onUpdate()

    }

    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return widgetView
    }
}