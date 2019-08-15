package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException

/**
 * @author lollipop
 * @date 2019-08-16 00:03
 * 偏好设置的Adapter
 */
class PreferenceAdapter (private val data: ArrayList<PreferenceInfo<*>>,
                        private val layoutInflater: LayoutInflater,
                        private val onPreferenceChanged: (info: PreferenceInfo<*>, newValue: Any) -> Unit)
    : RecyclerView.Adapter<PreferenceHolder<*, *>>() {

    private companion object {
        private const val HOLDER_UNKNOWN = -1
        private const val HOLDER_NUMBER = 0
        private const val HOLDER_BOOLEAN = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceHolder<*, *> {
        return when (viewType) {
            HOLDER_NUMBER -> PreferenceNumberHolder.create(layoutInflater, parent) { holder, newValue ->
                onPreferenceSelected(holder, newValue)
            }
            HOLDER_BOOLEAN -> PreferenceBooleanHolder.create(layoutInflater, parent) { holder, newValue ->
                onPreferenceSelected(holder, newValue)
            }
            else -> throw RuntimeException("unknown the type")
        }
    }

    private fun onPreferenceSelected(holder: PreferenceHolder<*, *>, newValue: Any) {
        onPreferenceChanged(data[holder.adapterPosition], newValue)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is PreferenceNumber -> HOLDER_NUMBER
            is PreferenceBoolean -> HOLDER_BOOLEAN
            else -> HOLDER_UNKNOWN
        }
    }

    override fun onBindViewHolder(holder: PreferenceHolder<*, *>, position: Int) {
        val info = data[holder.adapterPosition]
        when (holder) {
            is PreferenceBooleanHolder -> if (info is PreferenceBoolean) {
                holder.onBind(info)
            }
            is PreferenceNumberHolder -> if (info is PreferenceNumber) {
                holder.onBind(info)
            }
        }
    }
}