package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentBoolean
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.bean.AdjustmentInteger
import liang.lollipop.electronicclock.utils.SeekBarDialog

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 * 调整项的接口
 */
class AdjustmentSeekBarHolder(view: View): AdjustmentHolder<AdjustmentInteger>(view) {

    companion object {
        fun create(inflater: LayoutInflater, group: ViewGroup): AdjustmentSeekBarHolder {
            return AdjustmentSeekBarHolder(
                inflater.inflate(R.layout.item_adjustment_seek, group, false))
        }
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summaryView: TextView = view.findViewById(R.id.summaryView)
    private val valueView: TextView = view.findViewById(R.id.valueView)

    private var bindInfo: AdjustmentInteger? = null

    init {
        view.setOnClickListener {
            bindInfo?.let { info ->
                if (!info.enable) {
                    return@setOnClickListener
                }
                SeekBarDialog.getInstance(view.context) {
                    title = info.title
                    value = info.value
                    min = info.min
                    max = info.max
                }.onProgressConfirm {
                    onValueChange(it)
                }.show()
            }
        }
    }

    override fun onBind(info: AdjustmentInteger) {
        bindInfo = info
        setViewEnable(info.enable, titleView, valueView, summaryView)
        titleView.text = info.title
        summaryView.text = info.summary
        valueView.text = "${info.value}"
    }

    private fun onValueChange(newValue: Int) {
        bindInfo?.value = newValue
        valueView.text = "$newValue"
        onValueChangeListener?.onValueChange(this, newValue)
    }

}