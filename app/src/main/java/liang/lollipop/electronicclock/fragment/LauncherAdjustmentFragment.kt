package liang.lollipop.electronicclock.fragment

import android.view.View
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.widget.info.LauncherPanelInfo
import liang.lollipop.electronicclock.widget.panel.LauncherPanel
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-11-24 20:19
 */
class LauncherAdjustmentFragment: PanelInfoAdjustmentFragment() {

    private val launcherPanelInfo = LauncherPanelInfo()
    private val launcherPanel = LauncherPanel(launcherPanelInfo)

    override fun getPanelView(): View {
        return launcherPanel.getView(context!!)
    }

    override fun getPanelInfo(): PanelInfo {
        return launcherPanelInfo
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        info?.let { launcherPanelInfo.copy(it) }
        putAdjustmentInfo()
        panelInitComplete()
    }

    override fun initInfoByValue(info: String) {
        launcherPanelInfo.parse(JSONObject(info))
    }

    private fun putAdjustmentInfo() {
        val density = context!!.resources.displayMetrics.density
        addAdjustmentInfo(
            switch {
                title = getString(R.string.title_auto_color)
                summary = getString(R.string.summary_auto_color)
                key = LauncherPanelInfo.KEY_AUTO_COLOR
                value = launcherPanelInfo.isAutoColor
            },
            colors {
                title = getString(R.string.title_background_color)
                summary = getString(R.string.summary_background_color)
                key = LauncherPanelInfo.KEY_BTN_COLOR
                relevantKey = LauncherPanelInfo.KEY_AUTO_COLOR
                relevantEnable = false
                maxSize = 1
                minSize = 1
                reset(launcherPanelInfo.btnColor)
            },
            colors {
                title = getString(R.string.title_foreground_color)
                summary = getString(R.string.summary_foreground_color)
                key = LauncherPanelInfo.KEY_ICON_COLOR
                relevantKey = LauncherPanelInfo.KEY_AUTO_COLOR
                relevantEnable = false
                maxSize = 1
                minSize = 1
                reset(launcherPanelInfo.iconColor)
            },
            seekBar {
                title = getString(R.string.title_elevation)
                summary = getString(R.string.summary_elevation)
                key = LauncherPanelInfo.KEY_ELEVATION
                max = 50
                min = 0
                value = (launcherPanelInfo.elevation / density).toInt()
            },
            seekBar {
                title = getString(R.string.title_radius)
                summary = getString(R.string.summary_radius)
                key = LauncherPanelInfo.KEY_RADIUS
                max = 50
                min = 0
                value = (launcherPanelInfo.radius / density).toInt()
            }
        )
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
        val panelInfo = launcherPanelInfo
        when (info.key) {
            LauncherPanelInfo.KEY_AUTO_COLOR -> {
                panelInfo.isAutoColor = newValue.optBoolean(panelInfo.isAutoColor)
            }
            LauncherPanelInfo.KEY_BTN_COLOR  -> {
                if (newValue is IntArray && newValue.size > 0) {
                    panelInfo.btnColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    panelInfo.btnColor = newValue[0] as? Int ?: panelInfo.btnColor
                }
            }
            LauncherPanelInfo.KEY_ICON_COLOR -> {
                if (newValue is IntArray && newValue.size > 0) {
                    panelInfo.iconColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    panelInfo.iconColor = newValue[0] as? Int ?: panelInfo.iconColor
                }
            }
            LauncherPanelInfo.KEY_ELEVATION -> {
                val density = context!!.resources.displayMetrics.density
                panelInfo.elevation = newValue.optFloat(panelInfo.elevation) * density
            }
            LauncherPanelInfo.KEY_RADIUS -> {
                val density = context!!.resources.displayMetrics.density
                panelInfo.radius = newValue.optFloat(panelInfo.radius) * density
            }
        }
        launcherPanel.onInfoChange()
    }
}