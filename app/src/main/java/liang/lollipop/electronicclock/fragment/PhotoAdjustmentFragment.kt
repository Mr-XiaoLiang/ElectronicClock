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
        addAdjustmentInfo(

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