package liang.lollipop.electronicclock.fragment

import android.util.Log
import android.view.View
import liang.lollipop.electronicclock.R
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
                title = getString(R.string.title_select_photo)
                summary = getString(R.string.summary_select_photo)
                key = PhotoFramePanelInfo.KEY_IMAGES
                maxSize = 36
                resetFromString(photoInfo.images)
            },
            seekBar {
                title = getString(R.string.title_elevation)
                summary = getString(R.string.summary_elevation)
                key = PhotoFramePanelInfo.KEY_ELEVATION
                max = 50
                min = 0
                value = (photoInfo.elevation / density).toInt()
            },
            seekBar {
                title = getString(R.string.title_radius)
                summary = getString(R.string.summary_radius)
                key = PhotoFramePanelInfo.KEY_RADIUS
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
            PhotoFramePanelInfo.KEY_IMAGES -> {

            }
            PhotoFramePanelInfo.KEY_ELEVATION -> {

            }
            PhotoFramePanelInfo.KEY_RADIUS -> {

            }
        }
        photoPanel.onInfoChange()
    }

}