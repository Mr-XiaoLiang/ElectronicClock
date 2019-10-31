package liang.lollipop.electronicclock.widget.info

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
        const val KEY_TEXT_GRADLE = "KEY_TEXT_GRADLE"
    }

    val images = ArrayList<String>()

    var textGradle = 0

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
        textGradle = jsonObj.optInt(KEY_TEXT_GRADLE)
    }

    override fun serialize(jsonObj: JSONObject) {
        super.serialize(jsonObj)
        val imageArray = JSONArray()
        for (img in images) {
            imageArray.put(img)
        }
        jsonObj.put(KEY_IMAGES, imageArray)
        jsonObj.put(KEY_TEXT_GRADLE, textGradle)
    }

}