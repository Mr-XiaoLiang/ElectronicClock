package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author lollipop
 * @date 2019-08-08 12:44
 * action按钮的适配器
 */
class ActionAdapter(private val data: ArrayList<ActionInfo>,
                    private val layoutInflater: LayoutInflater,
                    private val clickListener: (ActionHolder) -> Unit):
    RecyclerView.Adapter<ActionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionHolder {
        return ActionHolder.create(layoutInflater, parent).apply {
            onItemClickListener = clickListener
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ActionHolder, position: Int) {
        holder.onBind(data[position])
    }
}