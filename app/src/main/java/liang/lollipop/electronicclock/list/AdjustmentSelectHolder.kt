package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentSelect
import liang.lollipop.electronicclock.utils.ListSelectDialog

/**
 * @author lollipop
 * @date 2019-10-10 22:09
 * 列表选择的选择项目
 */
class AdjustmentSelectHolder(view: View): AdjustmentHolder<AdjustmentSelect>(view) {

    companion object {
        fun create(inflater: LayoutInflater, group: ViewGroup): AdjustmentSelectHolder {
            return AdjustmentSelectHolder(
                inflater.inflate(R.layout.item_adjustment_select, group, false))
        }
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summaryView: TextView = view.findViewById(R.id.summaryView)

    private var bindInfo: AdjustmentSelect? = null

    init {
        view.setOnClickListener { clickView ->
            bindInfo?.let { info ->
                ListSelectDialog.create(clickView.context)
                    .setData(info.itemList)
                    .setTitle(info.title)
                    .selectedTo(info.selectedIndex)
                    .onItemSelected { dialog, index, value ->
                        bindInfo?.selectedIndex = index
                        summaryView.text = value
                        onValueChangeListener?.onValueChange(this, index)
                        dialog.dismiss()
                    }.show()
            }
        }
    }

    override fun onBind(info: AdjustmentSelect) {
        this.bindInfo = info
        titleView.text = info.title
        summaryView.text = info.selectedItem()
    }
}