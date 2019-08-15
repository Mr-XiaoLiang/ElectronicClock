package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.NumberSelectedDialog

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

        fun create(layoutInflater: LayoutInflater,
                   parent: ViewGroup,
                   listener: (PreferenceHolder<*, *>, newValue: Int) -> Unit): PreferenceNumberHolder {
            return create(layoutInflater, parent, object : OnSelectedListener<Int> {
                override fun onSelected(holder: PreferenceHolder<*, *>, newValue: Int) {
                    listener(holder, newValue)
                }
            })
        }

    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summerView: TextView = view.findViewById(R.id.summerView)
    private val valueView: TextView = view.findViewById(R.id.valueView)
    private var info: PreferenceNumber? = null

    init {
        view.setOnClickListener {
            info?.let {
                openNumberSelectDialog(it)
            }
        }
    }

    override fun onBind(info: PreferenceNumber) {
        this.info = info
        titleView.text = info.title
        summerView.text = info.summer
        onValueChange(info.value)
    }

    private fun onValueChange(value: Int) {
        valueView.text = "$value"
    }

    private fun openNumberSelectDialog(preference: PreferenceNumber) {
        NumberSelectedDialog.create(itemView.context).let { builder ->
            builder.min = preference.min
            builder.max = preference.max
            builder.title = preference.title
            builder.onNumberSelected = { dialog, number ->
                onValueChange(number)
                onSelectedListener.onSelected(this, number)
                dialog.dismiss()
            }
            builder
        }.show()
    }

}