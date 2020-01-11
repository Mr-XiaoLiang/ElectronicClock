package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentCheckList
import liang.lollipop.electronicclock.utils.CheckListDialog
import java.lang.StringBuilder

/**
 * @author lollipop
 * @date 2020-01-09 23:02
 * 多选的Holder
 */
class AdjustmentMultipleChoiceHolder(view: View): AdjustmentHolder<AdjustmentCheckList>(view) {

    companion object {
        fun create(inflater: LayoutInflater, group: ViewGroup): AdjustmentSelectHolder {
            return AdjustmentSelectHolder(
                inflater.inflate(R.layout.item_adjustment_select, group, false))
        }
    }

    private val builder: StringBuilder by lazy {
        StringBuilder()
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summaryView: TextView = view.findViewById(R.id.summaryView)

    private var bindInfo: AdjustmentCheckList? = null

    init {
        view.setOnClickListener { clickView ->
            bindInfo?.let { info ->
                CheckListDialog.show(info.selectedList,
                    info.unselectedList, info.maxSize,
                    clickView.context) { selected ->
                    info.onSelectedChange(selected)
                    onBind(info)
                    onValueChangeListener?.onValueChange(this, selected)
                }
            }
        }
    }

    override fun onBind(info: AdjustmentCheckList) {
        this.bindInfo = info
        titleView.text = info.title
        summaryView.text = getSelectedValue(info)
    }

    private fun getSelectedValue(info: AdjustmentCheckList): String {
        val selected = info.selectedList
        if (selected.isEmpty()) {
            return info.summary
        }
        builder.clear()
        for (index in selected.indices) {
            if (index > 0) {
                builder.append(", ")
            }
            builder.append(selected[index].name)
        }
        return builder.toString()
    }

}