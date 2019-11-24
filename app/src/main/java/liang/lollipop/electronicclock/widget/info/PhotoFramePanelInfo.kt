package liang.lollipop.electronicclock.widget.info

import android.content.Intent
import android.text.TextUtils
import liang.lollipop.electronicclock.activity.PanelInfoAdjustmentActivity
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-10-28 22:00
 * 相框的面板描述信息
 */
class PhotoFramePanelInfo: PanelInfo() {

    companion object {
        const val KEY_IMAGES = "KEY_IMAGES"
        const val KEY_ELEVATION = "KEY_ELEVATION"
        const val KEY_RADIUS = "KEY_RADIUS"
    }

    val images = ArrayList<String>()

    var elevation = 0F

    var radius = 0F

    init {
        needInit()
    }

    override fun parse(jsonObj: JSONObject) {
        super.parse(jsonObj)
        val imageArray = jsonObj.optJSONArray(KEY_IMAGES)
        images.clear()
        if (imageArray != null) {
            for (i in 0 until imageArray.length()) {
                val img = imageArray.optString(i)?:continue
                images.add(img)
            }
        }
        elevation = jsonObj.optDouble(KEY_ELEVATION, 0.0).toFloat()
        radius = jsonObj.optDouble(KEY_RADIUS, 0.0).toFloat()

        tryUpdateIntent()
    }

    override fun getIntent(): Intent? {
        return PanelInfoAdjustmentActivity.getIntent(this)
    }

    override fun serialize(jsonObj: JSONObject) {
        super.serialize(jsonObj)
        val imageArray = JSONArray()
        for (img in images) {
            imageArray.put(img)
        }
        jsonObj.put(KEY_IMAGES, imageArray)
        jsonObj.put(KEY_ELEVATION, elevation)
        jsonObj.put(KEY_RADIUS, radius)
    }

    override fun initData(data: Intent) {
        super.initData(data)
        val info = PanelInfoAdjustmentActivity.getInfo(data)
        if (!TextUtils.isEmpty(info)) {
            parse(JSONObject(info))
        }
    }

}