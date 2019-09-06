package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.AdjustmentBoolean
import liang.lollipop.electronicclock.bean.AdjustmentColor
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.bean.AdjustmentInteger

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 */
class AdjustmentAdapter(private val data: ArrayList<AdjustmentInfo>,
                        private val inflater: LayoutInflater,
                        private val onValueChange: (info: AdjustmentInfo, newValue: Any) -> Unit): RecyclerView.Adapter<AdjustmentHolder<*>>(),
    AdjustmentHolder.OnValueChangeListener {

    companion object {
        private const val TYPE_SWITCH = 0
        private const val TYPE_SEEKBAR = 1
        private const val TYPE_COLOR = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdjustmentHolder<*> {
        val holder =  when (viewType) {
            TYPE_SWITCH -> AdjustmentBooleanHolder.create(inflater, parent)
            TYPE_SEEKBAR -> AdjustmentSeekBarHolder.create(inflater, parent)
            TYPE_COLOR -> AdjustmentColorHolder.create(inflater, parent)
            else -> throw RuntimeException("unknown the viewType:$viewType")
        }
        holder.onValueChangeListener = this
        return holder
    }

    override fun onValueChange(holder: AdjustmentHolder<*>, newValue: Any) {
        onValueChange(data[holder.adapterPosition], newValue)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is AdjustmentBoolean -> TYPE_SWITCH
            is AdjustmentInteger -> TYPE_SEEKBAR
            is AdjustmentColor -> TYPE_COLOR
            else -> throw RuntimeException("unknown the AdjustmentInfo type")
        }
    }

    override fun onBindViewHolder(holder: AdjustmentHolder<*>, position: Int) {
        val info = data[position]
        when(holder) {
            is AdjustmentBooleanHolder -> if (info is AdjustmentBoolean) {
                holder.onBind(info)
            }
            is AdjustmentSeekBarHolder -> if (info is AdjustmentInteger) {
                holder.onBind(info)
            }
            is AdjustmentColorHolder -> if (info is AdjustmentColor) {
                holder.onBind(info)
            }
        }
    }

}