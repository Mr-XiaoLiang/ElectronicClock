package liang.lollipop.electronicclock.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.PreferenceInfo

/**
 * @author lollipop
 * @date 2019-08-15 22:13
 * Boolean类型的偏好设置holder
 */
abstract class PreferenceHolder<V, T: PreferenceInfo<V>>(view: View, protected val onSelectedListener: OnSelectedListener<V>): RecyclerView.ViewHolder(view) {

    abstract fun onBind(info: T)

    interface OnSelectedListener<V> {
        fun onSelected(holder: PreferenceHolder<*, *>, newValue: V)
    }

}