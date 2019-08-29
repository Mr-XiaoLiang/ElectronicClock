package liang.lollipop.electronicclock.utils

import android.content.Intent
import liang.lollipop.electronicclock.fragment.BatteryAdjustmentFragment
import liang.lollipop.electronicclock.fragment.EmptyAdjustmentFragment
import liang.lollipop.electronicclock.fragment.PanelInfoAdjustmentFragment
import liang.lollipop.electronicclock.widget.info.BatteryInfo
import liang.lollipop.widget.widget.PanelInfo

/**
 * 面板调整器的辅助类
 */
object PanelInfoAdjustmentHelper {

    enum class PanelType(val value: Int) {
        Empty(0),
        Battery(1)
    }

    fun createFragmentForIntent(intent: Intent, key: String, infoId: Int): PanelInfoAdjustmentFragment {
        return createFragmentByType(intent.getIntExtra(key, PanelType.Empty.value), infoId)
    }

    private fun createFragmentByType(typeId: Int, infoId: Int): PanelInfoAdjustmentFragment {
        return when (typeId) {
            PanelType.Battery.value -> {
                BatteryAdjustmentFragment.getInstance(infoId)
            }
            else -> {
                EmptyAdjustmentFragment.getInstance()
            }
        }
    }

    fun getTypeByInfo(info: PanelInfo): Int {
        return when (info) {
            is BatteryInfo -> PanelType.Battery.value
            else -> PanelType.Empty.value
        }
    }

}