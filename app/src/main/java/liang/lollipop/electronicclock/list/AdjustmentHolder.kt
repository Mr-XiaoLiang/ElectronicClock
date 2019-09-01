package liang.lollipop.electronicclock.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.AdjustmentInfo

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 * 调整项的接口
 */
abstract class AdjustmentHolder<T: AdjustmentInfo<*>>(view: View): RecyclerView.ViewHolder(view) {

    abstract fun onBind(info: T)

}