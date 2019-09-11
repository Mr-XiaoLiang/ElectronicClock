package liang.lollipop.electronicclock.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentBoolean
import liang.lollipop.electronicclock.bean.AdjustmentInfo

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 * 调整项的接口
 */
class AdjustmentBooleanHolder(view: View): AdjustmentHolder<AdjustmentBoolean>(view) {

    companion object {
        fun create(inflater: LayoutInflater, group: ViewGroup): AdjustmentBooleanHolder {
            return AdjustmentBooleanHolder(
                inflater.inflate(R.layout.item_adjustment_boolean, group, false))
        }
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summaryView: TextView = view.findViewById(R.id.summaryView)
    private val switchView: SwitchCompat = view.findViewById(R.id.switchView)

    private var bindInfo: AdjustmentBoolean? = null

    private val checkedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        onSwitchChange(isChecked)
        bindInfo?.value = isChecked
        onValueChangeListener?.onValueChange(this@AdjustmentBooleanHolder, isChecked)
    }

    override fun onBind(info: AdjustmentBoolean) {
        bindInfo = info
        setViewEnable(info.enable, titleView, switchView, summaryView)
        titleView.text = info.title
        switchView.setOnCheckedChangeListener(null)
        onSwitchChange(info.value)
        switchView.setOnCheckedChangeListener(checkedChangeListener)
    }

    private fun onSwitchChange(value: Boolean) {
        switchView.isChecked = value
        bindInfo?.let {
            summaryView.text = if (value) { it.summaryOfTrue } else { it.summaryOfFalse }
        }
        Log.d("Lollipop", "bindInfo is ${bindInfo?.summaryOfTrue}")
    }

}