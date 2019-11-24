package liang.lollipop.electronicclock.widget.info

import android.content.Intent
import android.graphics.Color
import liang.lollipop.electronicclock.activity.PanelInfoAdjustmentActivity
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-11-22 21:51
 * 启动器面板的Info
 */
class LauncherPanelInfo: PanelInfo() {

    companion object {
        const val KEY_BTN_COLOR = "KEY_BTN_COLOR"
        const val KEY_ICON_COLOR = "KEY_ICON_COLOR"
        const val KEY_AUTO_COLOR = "KEY_AUTO_COLOR"
        const val KEY_ELEVATION = "KEY_ELEVATION"
        const val KEY_RADIUS = "KEY_RADIUS"
    }

    var btnColor = Color.BLACK

    var iconColor = Color.WHITE

    var isAutoColor = true

    var elevation = 0F

    var radius = 0F

    init {
        needInit()
    }

    override fun parse(jsonObj: JSONObject) {
        super.parse(jsonObj)
        elevation = jsonObj.optDouble(KEY_ELEVATION, 0.0).toFloat()
        radius = jsonObj.optDouble(KEY_RADIUS, 0.0).toFloat()
        btnColor = jsonObj.optInt(KEY_BTN_COLOR, Color.BLACK)
        iconColor = jsonObj.optInt(KEY_ICON_COLOR, Color.WHITE)
        isAutoColor = jsonObj.optBoolean(KEY_AUTO_COLOR, true)

        tryUpdateIntent()
    }

    override fun getIntent(): Intent? {
        return PanelInfoAdjustmentActivity.getIntent(this)
    }

    override fun serialize(jsonObj: JSONObject) {
        super.serialize(jsonObj)
        jsonObj.put(KEY_ELEVATION, elevation)
        jsonObj.put(KEY_RADIUS, radius)
        jsonObj.put(KEY_BTN_COLOR, btnColor)
        jsonObj.put(KEY_ICON_COLOR, iconColor)
        jsonObj.put(KEY_AUTO_COLOR, isAutoColor)
    }

}