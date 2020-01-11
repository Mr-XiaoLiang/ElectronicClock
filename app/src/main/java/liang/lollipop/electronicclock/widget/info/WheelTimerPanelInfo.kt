package liang.lollipop.electronicclock.widget.info

import android.content.Intent
import android.text.TextUtils
import liang.lollipop.electronicclock.activity.PanelInfoAdjustmentActivity
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-12-08 17:55
 * 滚轮时间的面板信息
 */
class WheelTimerPanelInfo: PanelInfo() {

    companion object {
        const val MONTH = "MONTH"
        const val DAY = "DAY"
        const val WEEK = "WEEK"
        const val HOUR = "HOUR"
        const val MINUTE = "MINUTE"
        const val SECOND = "SECOND"
        const val ANIMATION = "ANIMATION"
        const val SIMULATION = "SIMULATION"
        const val SHOW_GRID = "SHOW_GRID"
    }

    val monthValues = ArrayList<Int>()
    val dayValues = ArrayList<Int>()
    val weekValues = ArrayList<Int>()
    val hourValues = ArrayList<Int>()
    val minuteValues = ArrayList<Int>()
    val secondValues = ArrayList<Int>()

    var animation = true

    var simulation = false

    var showGrid = false

    init {
        needInit()
    }

    override fun parse(jsonObj: JSONObject) {
        super.parse(jsonObj)
        animation = jsonObj.optBoolean(ANIMATION, true)
        simulation = jsonObj.optBoolean(SIMULATION, false)
        showGrid = jsonObj.optBoolean(SHOW_GRID, false)
        monthValues.optValue(jsonObj, MONTH)
        dayValues.optValue(jsonObj, DAY)
        weekValues.optValue(jsonObj, WEEK)
        hourValues.optValue(jsonObj, HOUR)
        minuteValues.optValue(jsonObj, MINUTE)
        secondValues.optValue(jsonObj, SECOND)
        tryUpdateIntent()
    }

    override fun getIntent(): Intent? {
        return PanelInfoAdjustmentActivity.getIntent(this)
    }

    override fun serialize(jsonObj: JSONObject) {
        super.serialize(jsonObj)
        jsonObj.put(ANIMATION, animation)
        jsonObj.put(SIMULATION, simulation)
        jsonObj.put(SHOW_GRID, showGrid)
        monthValues.putValue(jsonObj, MONTH)
        dayValues.putValue(jsonObj, DAY)
        weekValues.putValue(jsonObj, WEEK)
        hourValues.putValue(jsonObj, HOUR)
        minuteValues.putValue(jsonObj, MINUTE)
        secondValues.putValue(jsonObj, SECOND)
    }

    private fun ArrayList<Int>.putValue(jsonObj: JSONObject, key: String) {
        val array = JSONArray()
        for (id in this) {
            array.put(id)
        }
        jsonObj.put(key, array)
    }

    private fun ArrayList<Int>.optValue(jsonObj: JSONObject, key: String) {
        this.clear()
        jsonObj.optJSONArray(key)?.let { list ->
            for (index in 0 until list.length()) {
                val id = list.optInt(index)
                if (id == 0) {
                    continue
                }
                this.add(id)
            }
        }
    }

    override fun initData(data: Intent) {
        super.initData(data)
        val info = PanelInfoAdjustmentActivity.getInfo(data)
        if (!TextUtils.isEmpty(info)) {
            parse(JSONObject(info))
        }
    }

}