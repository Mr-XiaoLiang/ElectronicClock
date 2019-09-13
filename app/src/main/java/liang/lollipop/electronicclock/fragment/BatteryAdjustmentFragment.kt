package liang.lollipop.electronicclock.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.widget.info.BatteryPanelInfo
import liang.lollipop.electronicclock.widget.panel.BatteryPanel
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * 电池的调整面板
 * 用于调整电池小部件的参数
 */
class BatteryAdjustmentFragment: PanelInfoAdjustmentFragment() {

    companion object {
        fun getInstance(id: Int): BatteryAdjustmentFragment {
            return BatteryAdjustmentFragment().apply {
                arguments = Bundle().apply {
                    if (id != PanelInfo.NO_ID) {
                        putString(ARG_INFO_ID, "$id")
                    }
                }
            }
        }
    }

    private val batteryInfo = BatteryPanelInfo()
    private val batteryPanel = BatteryPanel(batteryInfo).apply {

    }

    override fun getPanelView(): View {
        return batteryPanel.createView(context!!)
    }

    override fun initInfoByValue(info: String) {
        batteryInfo.parse(JSONObject(info))
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        info?.let { batteryInfo.copy(it) }
        putAdjustmentInfo()
        panelInitComplete()
    }

    private fun putAdjustmentInfo() {
        addAdjustmentInfo(
            switch {
                key = BatteryPanelInfo.IS_SHOW_BG
                title = getString(R.string.title_show_bg)
                summaryOfTrue = getString(R.string.summary_show_bg)
                summaryOfFalse = getString(R.string.summary_hide_bg)
                value = batteryInfo.isShowBg
            },
            switch {
                key = BatteryPanelInfo.IS_SHOW_BORDER
                title = getString(R.string.title_show_border)
                summaryOfTrue = getString(R.string.summary_show_border)
                summaryOfFalse = getString(R.string.summary_hide_border)
                value = batteryInfo.isShowBorder
            },
            seekBar {
                key = BatteryPanelInfo.CORNER
                title = getString(R.string.title_corner)
                summary = getString(R.string.summary_corner)
                min = 0
                max = 100
                value = batteryInfo.corner.packingToInt()
            },
            colors {
                key = BatteryPanelInfo.COLOR_ARRAY
                title = getString(R.string.title_color_array)
                summary = getString(R.string.summary_color_array)
                maxSize = 100
                reset(batteryInfo.colorArray)
            },
            paddings {
                key = BatteryPanelInfo.PADDING
                title = getString(R.string.title_paddings)
                reset(batteryInfo.padding)
            },
            switch {
                key = BatteryPanelInfo.IS_ARC
                title = getString(R.string.title_arc_mode)
                summaryOfTrue = getString(R.string.summary_arc_true)
                summaryOfFalse = getString(R.string.summary_arc_false)
                value = batteryInfo.isArc
            },
            seekBar {
                key = BatteryPanelInfo.ARC_WIDTH
                relevantKey = BatteryPanelInfo.IS_ARC
                title = getString(R.string.title_arc_width)
                summary = getString(R.string.summary_arc_width)
                min = 0
                max = 100
                value = batteryInfo.arcWidth.packingToInt()
            },
            seekBar {
                key = BatteryPanelInfo.BORDER_WIDTH
                title = getString(R.string.title_border_width)
                summary = getString(R.string.summary_border_width)
                min = 0
                max = 100
                value = batteryInfo.borderWidth.packingToInt()
            },
            colors {
                key = BatteryPanelInfo.BORDER_COLOR
                title = getString(R.string.title_border_color)
                summary = getString(R.string.summary_border_color)
                reset(batteryInfo.borderColor)
            },
            switch {
                key = BatteryPanelInfo.IS_VERTICAL
                relevantKey = BatteryPanelInfo.IS_ARC
                relevantEnable = false
                title = getString(R.string.title_is_vertical)
                summaryOfTrue = getString(R.string.summary_vertical)
                summaryOfFalse = getString(R.string.summary_horizontal)
                value = batteryInfo.isVertical
            },
            switch {
                key = BatteryPanelInfo.IS_ANIMATION
                title = getString(R.string.title_is_animation)
                enable = false
                summaryOfTrue = getString(R.string.summary_animation_enable)
                summaryOfFalse = getString(R.string.summary_animation_disable)
                value = batteryInfo.isAnimation
            }
        )
    }

    override fun getPanelInfo(): PanelInfo {
        return batteryInfo
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
        Log.d("Lollipop", "onInfoChange(${info.key})")
        when (info.key) {
            BatteryPanelInfo.IS_SHOW_BG     -> {
                batteryInfo.isShowBg = newValue.optBoolean(batteryInfo.isShowBg)
            }
            BatteryPanelInfo.IS_SHOW_BORDER -> {
                batteryInfo.isShowBorder = newValue.optBoolean(batteryInfo.isShowBorder)
            }
            BatteryPanelInfo.CORNER         -> {
                batteryInfo.corner =newValue.optUnpackingToFloat(batteryInfo.corner)
            }
            BatteryPanelInfo.COLOR_ARRAY    -> {
                if (newValue is IntArray) {
                    batteryInfo.colorArray.clear()
                    newValue.forEach { color ->
                        batteryInfo.colorArray.add(color)
                    }
                } else if (newValue is ArrayList<*>) {
                    batteryInfo.colorArray.clear()
                    newValue.forEach { color ->
                        if (color is Int) {
                            batteryInfo.colorArray.add(color)
                        }
                    }
                }
            }
            BatteryPanelInfo.PADDING        -> {
                if (newValue is FloatArray) {
                    batteryInfo.padding[0] = newValue[0]
                    batteryInfo.padding[1] = newValue[1]
                    batteryInfo.padding[2] = newValue[2]
                    batteryInfo.padding[3] = newValue[3]
                }
            }
            BatteryPanelInfo.IS_VERTICAL    -> {
                batteryInfo.isVertical = newValue.optBoolean(batteryInfo.isVertical)
            }
            BatteryPanelInfo.BORDER_WIDTH   -> {
                batteryInfo.borderWidth =newValue.optUnpackingToFloat(batteryInfo.borderWidth)
            }
            BatteryPanelInfo.BORDER_COLOR   -> {
                if (newValue is IntArray && newValue.size > 0) {
                    batteryInfo.borderColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    batteryInfo.borderColor = newValue[0] as? Int ?: batteryInfo.borderColor
                }
            }
            BatteryPanelInfo.IS_ANIMATION   -> {
                batteryInfo.isAnimation = newValue.optBoolean(batteryInfo.isAnimation)
            }
            BatteryPanelInfo.IS_ARC         -> {
                batteryInfo.isArc = newValue.optBoolean(batteryInfo.isArc)
            }
            BatteryPanelInfo.ARC_WIDTH      -> {
                batteryInfo.arcWidth =newValue.optUnpackingToFloat(batteryInfo.arcWidth)
            }
        }
        batteryPanel.onInfoChange()
    }

}