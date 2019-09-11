package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentPadding
import liang.lollipop.electronicclock.utils.EditPaddingDialog

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 * 调整项的接口
 */
class AdjustmentPaddingHolder(view: View): AdjustmentHolder<AdjustmentPadding>(view) {

    companion object {
        fun create(inflater: LayoutInflater, group: ViewGroup): AdjustmentPaddingHolder {
            return AdjustmentPaddingHolder(
                inflater.inflate(R.layout.item_adjustment_padding, group, false))
        }
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summaryView: TextView = view.findViewById(R.id.summaryView)

    private var bindInfo: AdjustmentPadding? = null

    init {
        view.setOnClickListener{ clickView ->
            bindInfo?.let { info ->
                if (!info.enable) {
                    return@setOnClickListener
                }
                EditPaddingDialog.create(clickView.context) {
                    putPaddingValue(info.paddings)
                }.onPaddingConfirmed { paddings ->
                    onPaddingChange(paddings)
                    bindInfo?.reset(paddings)
                    onValueChangeListener?.onValueChange(this, paddings)
                }.show()
            }
        }
    }

    override fun onBind(info: AdjustmentPadding) {
        bindInfo = info
        setViewEnable(info.enable, titleView, summaryView)
        titleView.text = info.title
        onPaddingChange(info.paddings)
    }

    private fun onPaddingChange(p: FloatArray) {
        summaryView.text = itemView.context.getString(R.string.summary_paddings,
            p[0] * 100, p[1] * 100, p[2] * 100, p[3] * 100)
    }

}