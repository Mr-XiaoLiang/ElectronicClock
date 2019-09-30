package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.LunarAuspiciousDayInfo
import liang.lollipop.electronicclock.bean.LunarFestivalInfo

/**
 * @author lollipop
 * @date 2019-09-29 23:58
 * 日历页面节假日展示的Adapter
 */
class LunarAuspiciousDayAdapter(private val data: ArrayList<LunarAuspiciousDayInfo>,
                                private val layoutInflater: LayoutInflater):
    RecyclerView.Adapter<LunarAuspiciousDayHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LunarAuspiciousDayHolder {
        return LunarAuspiciousDayHolder.create(layoutInflater, parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LunarAuspiciousDayHolder, position: Int) {
        holder.onBind(data[position])
    }
}