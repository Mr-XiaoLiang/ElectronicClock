package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.AdjustmentInfo

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 */
class AdjustmentAdapter(private val data: ArrayList<AdjustmentInfo<*>>,
                        private val inflater: LayoutInflater): RecyclerView.Adapter<AdjustmentHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdjustmentHolder<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: AdjustmentHolder<*>, position: Int) {
    }
}