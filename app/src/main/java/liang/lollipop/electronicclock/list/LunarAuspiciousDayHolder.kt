package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.LunarAuspiciousDayInfo
import liang.lollipop.electronicclock.utils.LunarCalendar

/**
 * @author lollipop
 * @date 2019-09-29 23:52
 * 日历页面节假日展示的Holder
 */
class LunarAuspiciousDayHolder(view: View): RecyclerView.ViewHolder(view) {

    companion object {
        fun create(layoutInflater: LayoutInflater, parent: ViewGroup): LunarAuspiciousDayHolder {
            val layoutId = R.layout.item_auspicious_day
            return LunarAuspiciousDayHolder(layoutInflater.inflate(layoutId, parent, false))
        }
    }

    private val nameView: TextView = view.findViewById(R.id.nameView)


    fun onBind(info: LunarAuspiciousDayInfo) {
        nameView.text = LunarCalendar.stringToVertical(info.name, nameView.maxLines)
    }

}