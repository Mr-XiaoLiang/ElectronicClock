package liang.lollipop.electronicclock.utils

import android.content.Intent
import liang.lollipop.electronicclock.fragment.*
import liang.lollipop.electronicclock.widget.info.BatteryPanelInfo
import liang.lollipop.electronicclock.widget.info.CalendarPanelInfo
import liang.lollipop.electronicclock.widget.info.PhotoFramePanelInfo
import liang.lollipop.widget.widget.PanelInfo

/**
 * 面板调整器的辅助类
 */
object PanelInfoAdjustmentHelper {

    var defInfoType = PanelType.Battery.value

    enum class PanelType(val value: Int) {
        Empty(0),
        Battery(1),
        Calendar(2),
        Photo(3),
    }

    fun createFragmentForIntent(intent: Intent, key: String, infoId: Int): PanelInfoAdjustmentFragment {
        return createFragmentByType(intent.getIntExtra(key, defInfoType), infoId)
    }

    private fun createFragmentByType(typeId: Int, infoId: Int): PanelInfoAdjustmentFragment {
        return when (typeId) {
            PanelType.Battery.value -> {
                BatteryAdjustmentFragment().bindId(infoId)
            }
            PanelType.Calendar.value -> {
                CalendarAdjustmentFragment().bindId(infoId)
            }
            PanelType.Photo.value -> {
                PhotoAdjustmentFragment().bindId(infoId)
            }
            else -> {
                EmptyAdjustmentFragment.getInstance()
            }
        }
    }

    fun getTypeByInfo(info: PanelInfo): Int {
        return when (info) {
            is BatteryPanelInfo -> PanelType.Battery.value
            is CalendarPanelInfo -> PanelType.Calendar.value
            is PhotoFramePanelInfo -> PanelType.Photo.value
            else -> defInfoType
        }
    }

}