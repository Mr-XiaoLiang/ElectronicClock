package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.electronicclock.R

/**
 * @author lollipop
 * @date 2019-08-15 22:13
 * Boolean类型的偏好设置holder
 */
class PreferenceNumberHolder
    private constructor(view: View, listener: OnSelectedListener<Int>):
    PreferenceHolder<Int, PreferenceNumber>(view, listener) {

    companion object {
        fun create(layoutInflater: LayoutInflater,
                   parent: ViewGroup,
                   listener: OnSelectedListener<Int>): PreferenceNumberHolder {
            return PreferenceNumberHolder(
                layoutInflater.inflate(R.layout.item_preference_text, parent, false), listener)
        }
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summerView: TextView = view.findViewById(R.id.summerView)
    private val valueView: TextView = view.findViewById(R.id.valueView)
    private var info: PreferenceNumber? = null


    override fun onBind(info: PreferenceNumber) {
        this.info = info
        // 绑定数据时断开监听器，防止造成死循环
        titleView.text = info.title
        summerView.text = info.summer
        onValueChange(info.value)
    }

    private fun onValueChange(value: Int) {
        valueView.text = "$value"
    }

}