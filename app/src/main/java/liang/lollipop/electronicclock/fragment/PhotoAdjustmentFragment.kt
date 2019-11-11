package liang.lollipop.electronicclock.fragment

import android.util.Log
import android.view.View
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.widget.info.PhotoFramePanelInfo
import liang.lollipop.electronicclock.widget.panel.PhotoFramePanel
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * 相册的调整面板
 * 用于调整相册小部件的参数
 */
class PhotoAdjustmentFragment: PanelInfoAdjustmentFragment() {

    private val photoInfo = PhotoFramePanelInfo()
    private val photoPanel = PhotoFramePanel(photoInfo)

    override fun getPanelView(): View {
        return photoPanel.createView(context!!)
    }

    override fun initInfoByValue(info: String) {
        photoInfo.parse(JSONObject(info))
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        info?.let { photoInfo.copy(it) }
        putAdjustmentInfo()
        panelInitComplete()
    }

    private fun putAdjustmentInfo() {
        val context = context!!
        val density = context.resources.displayMetrics.density
        addAdjustmentInfo(
            photos {
                title = "图片选择"
                summary = "图片将会以翻页的形式展示"
                maxSize = 36
                resetFromString(photoInfo.images)
            },
            seekBar {
                title = "海拔高度"
                summary = "海拔越高，阴影越大，但是也越淡"
                max = 50
                min = 0
                value = (photoInfo.elevation / density).toInt()
            },
            seekBar {
                title = "圆角大小"
                summary = "过大的圆角，可能导致意料之外的情况"
                max = 50
                min = 0
                value = (photoInfo.radius / density).toInt()
            }
        )
    }

    override fun getPanelInfo(): PanelInfo {
        return photoInfo
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
        Log.d("Lollipop", "onInfoChange(${info.key})")
        when (info.key) {

        }
        photoPanel.onInfoChange()
    }

}