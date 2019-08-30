package liang.lollipop.electronicclock.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 滚轮式的布局管理器
 * @author Lollipop
 * @date 2019/08/30
 */
class WheelLayoutManager(
    private val orientation: Int = RecyclerView.VERTICAL,
    private val shownCount: Int = 5,
    private val zoomFactor: Float = 0.5F): RecyclerView.LayoutManager() {



    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return  RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

}