package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.PreferenceChoice

/**
 * @author lollipop
 * @date 2019-08-15 22:13
 * Boolean类型的偏好设置holder
 */
class PreferenceChoiceHolder
    private constructor(view: View, listener: OnSelectedListener<Int>):
    PreferenceHolder<Int, PreferenceChoice>(view, listener) {

    companion object {
        fun create(layoutInflater: LayoutInflater,
                    parent: ViewGroup,
                    listener: OnSelectedListener<Int>): PreferenceChoiceHolder {
            return PreferenceChoiceHolder(
                layoutInflater.inflate(R.layout.item_preference_image, parent, false), listener)
        }

        fun create(layoutInflater: LayoutInflater,
                   parent: ViewGroup,
                   listener: (PreferenceHolder<*, *>, newValue: Int) -> Unit): PreferenceChoiceHolder {
            return create(layoutInflater, parent, object : OnSelectedListener<Int> {
                override fun onSelected(holder: PreferenceHolder<*, *>, newValue: Int) {
                    listener(holder, newValue)
                }
            })
        }

    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summerView: TextView = view.findViewById(R.id.summerView)
    private val valueView: ImageView = view.findViewById(R.id.valueView)
    private var info: PreferenceChoice? = null

    init {
        view.setOnClickListener {
            info?.let {
                openSelectDialog(it)
            }
        }
    }

    override fun onBind(info: PreferenceChoice) {
        this.info = info
        titleView.text = info.title
        summerView.text = info.summer
        onValueChange(info.value)
    }

    private fun onValueChange(value: Int) {
        val resId: Int = info?.let {
            when (value) {
                PreferenceChoice.VALUE_POSITIVE -> it.positiveIcon
                PreferenceChoice.VALUE_NEGATIVE -> it.negativeIcon
                PreferenceChoice.VALUE_NEUTRAL -> it.neutralIcon
                else -> 0
            }
        }?:0
        valueView.setImageResource(resId)
    }

    private fun openSelectDialog(preference: PreferenceChoice) {
        AlertDialog.Builder(itemView.context)
            .setTitle(preference.title)
            .setMessage(preference.dialogMessage?:preference.summer)
            .setPositiveButton(preference.positiveName) { dialog, _ ->
                onDialogSelected(PreferenceChoice.VALUE_POSITIVE)
                dialog.dismiss()
            }
            .setNegativeButton(preference.negativeName) { dialog, _ ->
                onDialogSelected(PreferenceChoice.VALUE_NEGATIVE)
                dialog.dismiss()
            }
            .setNeutralButton(preference.neutralName) { dialog, _ ->
                onDialogSelected(PreferenceChoice.VALUE_NEUTRAL)
                dialog.dismiss()
            }
            .show()
    }

    private fun onDialogSelected(value: Int) {
        onValueChange(value)
        onSelectedListener.onSelected(this, value)
    }

}