package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.ActionInfo

/**
 * @author lollipop
 * @date 2019-08-08 11:52
 * action 按钮的Holder
 */
class ActionHolder(view: View): RecyclerView.ViewHolder(view) {

    companion object {
        fun create(layoutInflater: LayoutInflater, parent: ViewGroup, isAction: Boolean = true): ActionHolder {
            val layoutId = if (isAction) { R.layout.item_action_btn } else { R.layout.item_widget_btn }
            return ActionHolder(layoutInflater.inflate(layoutId, parent, false))
        }
    }

    private val iconView: ImageView = view.findViewById(R.id.actionIcon)
    private val nameView: TextView = view.findViewById(R.id.actionName)

    var onItemClickListener: ((ActionHolder) -> Unit)? = null

    init {
        view.setOnClickListener {
            onItemClickListener?.invoke(this)
        }
    }

    fun onBind(info: ActionInfo) {
        iconView.setImageResource(info.icon)
        nameView.setText(info.name)
    }

}