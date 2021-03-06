package liang.lollipop.electronicclock.utils

import android.content.Intent
import liang.lollipop.electronicclock.fragment.*
import liang.lollipop.electronicclock.widget.info.*
import liang.lollipop.widget.widget.PanelInfo

/**
 * 面板调整器的辅助类
 */
object PanelInfoAdjustmentHelper {

    private val defInfoType = PanelType.Empty.value

    enum class PanelType(val value: Int) {
        Empty(0),
        Battery(1),
        Calendar(2),
        Photo(3),
        Launcher(4),
        WheelTimer(5),
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
            PanelType.Launcher.value -> {
                LauncherAdjustmentFragment().bindId(infoId)
            }
            PanelType.WheelTimer.value -> {
                WheelTimerAdjustmentFragment().bindId(infoId)
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
            is LauncherPanelInfo -> PanelType.Launcher.value
            is WheelTimerPanelInfo -> PanelType.WheelTimer.value
            else -> defInfoType
        }
    }

}