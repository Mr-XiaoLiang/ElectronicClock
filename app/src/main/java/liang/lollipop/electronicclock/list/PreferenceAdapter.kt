package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.PreferenceBoolean
import liang.lollipop.electronicclock.bean.PreferenceChoice
import liang.lollipop.electronicclock.bean.PreferenceInfo
import liang.lollipop.electronicclock.bean.PreferenceNumber
import java.lang.RuntimeException

/**
 * @author lollipop
 * @date 2019-08-16 00:03
 * 偏好设置的Adapter
 */
class PreferenceAdapter (private val data: ArrayList<PreferenceInfo<*>>,
                         private val layoutInflater: LayoutInflater,
                         private val onPreferenceChanged: (info: PreferenceInfo<*>, newValue: Any) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val HOLDER_UNKNOWN = -1
        private const val HOLDER_NUMBER = 0
        private const val HOLDER_BOOLEAN = 1
        private const val HOLDER_CHOICE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HOLDER_NUMBER -> PreferenceNumberHolder.create(layoutInflater, parent) { holder, newValue ->
                onPreferenceSelected(holder, newValue)
            }
            HOLDER_BOOLEAN -> PreferenceBooleanHolder.create(layoutInflater, parent) { holder, newValue ->
                onPreferenceSelected(holder, newValue)
            }
            HOLDER_CHOICE -> PreferenceChoiceHolder.create(layoutInflater, parent) { holder, newValue ->
                onPreferenceSelected(holder, newValue)
            }
            else -> throw RuntimeException("unknown the type")
        }
    }

    private fun onPreferenceSelected(holder: RecyclerView.ViewHolder, newValue: Any) {
        onPreferenceChanged(data[holder.adapterPosition], newValue)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is PreferenceNumber -> HOLDER_NUMBER
            is PreferenceBoolean -> HOLDER_BOOLEAN
            is PreferenceChoice -> HOLDER_CHOICE
            else -> HOLDER_UNKNOWN
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info = data[holder.adapterPosition]
        when (holder) {
            is PreferenceBooleanHolder -> if (info is PreferenceBoolean) {
                holder.onBind(info)
            }
            is PreferenceNumberHolder -> if (info is PreferenceNumber) {
                holder.onBind(info)
            }
            is PreferenceChoiceHolder -> if (info is PreferenceChoice) {
                holder.onBind(info)
            }
        }
    }
}