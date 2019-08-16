package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import liang.lollipop.electronicclock.R

/**
 * @author lollipop
 * @date 2019-08-15 22:13
 * Boolean类型的偏好设置holder
 */
class PreferenceBooleanHolder
    private constructor(view: View, listener: OnSelectedListener<Boolean>):
    PreferenceHolder<Boolean, PreferenceBoolean>(view, listener) {

    companion object {
        fun create(layoutInflater: LayoutInflater, parent: ViewGroup,
                   listener: OnSelectedListener<Boolean>): PreferenceBooleanHolder {
            return PreferenceBooleanHolder(
                layoutInflater.inflate(R.layout.item_preference_boolean, parent, false), listener)
        }
        fun create(layoutInflater: LayoutInflater, parent: ViewGroup,
                   listener: (PreferenceHolder<*, *>, newValue: Boolean) -> Unit): PreferenceBooleanHolder {
            return create(layoutInflater, parent, object : OnSelectedListener<Boolean> {
                override fun onSelected(holder: PreferenceHolder<*, *>, newValue: Boolean) {
                    listener(holder, newValue)
                }
            })
        }
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summerView: TextView = view.findViewById(R.id.summerView)
    private val switchView: SwitchCompat = view.findViewById(R.id.switchView)
    private var info: PreferenceBoolean? = null
    private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        onSwitchChange(isChecked)
        onSelectedListener.onSelected(this@PreferenceBooleanHolder, isChecked)
    }


    private fun onSwitchChange(isChecked: Boolean) {
        info?.let {
            summerView.text = if (isChecked) {
                it.summerOfTrue
            } else {
                it.summerOfFalse
            }
        }
    }

    override fun onBind(info: PreferenceBoolean) {
        this.info = info
        // 绑定数据时断开监听器，防止造成死循环
        switchView.setOnCheckedChangeListener(null)
        titleView.text = info.title
        switchView.isChecked = info.value
        onSwitchChange(info.value)
        switchView.setOnCheckedChangeListener(onCheckedChangeListener)
    }

}