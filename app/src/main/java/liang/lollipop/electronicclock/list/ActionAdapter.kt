package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.ActionInfo

/**
 * @author lollipop
 * @date 2019-08-08 12:44
 * action按钮的适配器
 */
class ActionAdapter(private val data: ArrayList<ActionInfo>,
                    private val layoutInflater: LayoutInflater,
                    private val isAction: Boolean = true,
                    private val clickListener: (ActionHolder) -> Unit):
    RecyclerView.Adapter<ActionHolder>() {

    private companion object {
        private const val TYPE_ACTION = 0
        private const val TYPE_WIDGET = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionHolder {
        return ActionHolder.create(layoutInflater, parent, viewType == TYPE_ACTION).apply {
            onItemClickListener = clickListener
        }
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return if (isAction) {
            TYPE_ACTION
        } else {
            TYPE_WIDGET
        }
    }

    override fun onBindViewHolder(holder: ActionHolder, position: Int) {
        holder.onBind(data[position])
    }
}