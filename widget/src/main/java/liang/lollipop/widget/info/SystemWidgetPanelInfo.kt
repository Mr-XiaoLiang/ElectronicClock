package liang.lollipop.widget.info

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Parcel
import android.text.TextUtils
import android.util.Base64
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-08-05 13:25
 * 系统小部件的面板信息
 */
class SystemWidgetPanelInfo: PanelInfo() {

    companion object {
        private val EMPTY_WIDGET_PROVIDER_INFO = AppWidgetProviderInfo()

        private const val KEY_APP_WIDGET_ID = "KEY_APP_WIDGET_ID"
        private const val KEY_APP_WIDGET_PROVIDER_INFO = "KEY_APP_WIDGET_PROVIDER_INFO"
        private const val KEY_UPDATE_SPAN_BY_GROUP = "KEY_UPDATE_SPAN_BY_GROUP"
    }

    private val logger = Utils.loggerI("SystemWidgetPanelInfo")

    var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    var appWidgetProviderInfo: AppWidgetProviderInfo = EMPTY_WIDGET_PROVIDER_INFO

    var updateSpanByGroup = false

    override fun serialize(jsonObj: JSONObject) {
        super.serialize(jsonObj)
        jsonObj.put(KEY_APP_WIDGET_ID, appWidgetId)
        jsonObj.put(KEY_APP_WIDGET_PROVIDER_INFO,
            appWidgetProviderInfo.serialize())
        jsonObj.put(KEY_UPDATE_SPAN_BY_GROUP, updateSpanByGroup)
    }

    override fun parse(jsonObj: JSONObject) {
        logger("parse: $jsonObj")
        super.parse(jsonObj)
        appWidgetId = jsonObj.optInt(KEY_APP_WIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
        appWidgetProviderInfo = instantiation(
            jsonObj.optString(KEY_APP_WIDGET_PROVIDER_INFO, ""))
        updateSpanByGroup = jsonObj.optBoolean(KEY_UPDATE_SPAN_BY_GROUP, false)
    }

    private fun instantiation(value: String): AppWidgetProviderInfo {
        if (TextUtils.isEmpty(value)) {
            return EMPTY_WIDGET_PROVIDER_INFO
        }
        // 1.解码
        val bytes = Base64.decode(value, Base64.DEFAULT)
        // 2.反序列化
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0) // this is extremely important!
        val result = AppWidgetProviderInfo(parcel)
        parcel.recycle()
        return result
    }

    private fun AppWidgetProviderInfo.serialize(): String {
        if (this == EMPTY_WIDGET_PROVIDER_INFO) {
            return ""
        }
        // 1.序列化
        val p = Parcel.obtain()
        this.writeToParcel(p, 0)
        val bytes = p.marshall()
        p.recycle()
        // 2.编码
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    val isEmpty: Boolean
        get() {
            return appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ||
                    appWidgetProviderInfo == EMPTY_WIDGET_PROVIDER_INFO
        }

}