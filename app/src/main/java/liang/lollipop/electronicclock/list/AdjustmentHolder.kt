package liang.lollipop.electronicclock.list

import android.view.View
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.AdjustmentInfo

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 * 调整项的接口
 */
abstract class AdjustmentHolder<T: AdjustmentInfo>(view: View): RecyclerView.ViewHolder(view) {

    var onValueChangeListener: OnValueChangeListener? = null

    abstract fun onBind(info: T)

    interface OnValueChangeListener {
        fun onValueChange(holder: AdjustmentHolder<*>, newValue: Any)
    }

    protected fun setViewEnable(isEnable: Boolean, vararg views: View) {
        views.forEach {
            setViewEnable(it, isEnable)
        }
    }

    private fun setViewEnable(view: View, isEnable: Boolean) {
        when (view) {
            is Switch, is SwitchCompat -> {
                view.isEnabled = isEnable
            }
            else -> {
                view.alpha = if (isEnable) {
                    1F
                } else {
                    0.5F
                }
            }
        }
    }

}