package liang.lollipop.electronicclock.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.LunarFestivalInfo

/**
 * @author lollipop
 * @date 2019-09-29 23:52
 * 日历页面节假日展示的Holder
 */
class LunarFestivalHolder(view: View): RecyclerView.ViewHolder(view) {

    companion object {
        fun create(layoutInflater: LayoutInflater, parent: ViewGroup): LunarFestivalHolder {
            val layoutId = R.layout.item_festival
            return LunarFestivalHolder(layoutInflater.inflate(layoutId, parent, false))
        }
    }

    private val nameView: TextView = view.findViewById(R.id.nameView)


    fun onBind(info: LunarFestivalInfo) {
        nameView.text = info.name
        val context = itemView.context
        when(info.type) {
            LunarFestivalInfo.TYPE_GREGORIAN -> {
                nameView.setTextColor(ContextCompat.getColor(context, R.color.colorFestivalGregorian))
            }
            LunarFestivalInfo.TYPE_LUNAR -> {
                nameView.setTextColor(ContextCompat.getColor(context, R.color.colorFestivalLunar))
            }
            else -> {
                nameView.setTextColor(Color.GRAY)
            }
        }
    }

}