package liang.lollipop.electronicclock.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.pow

/**
 * 滚轮式的布局管理器
 * @author Lollipop
 * @date 2019/08/30
 */
class WheelLayoutManager(
    private val orientation: Int = RecyclerView.VERTICAL,
    private val shownCount: Int = 5,
    private val zoomFactor: Float = 0.7F): RecyclerView.LayoutManager() {

    private var selectedPosition = -1

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?:return
        state?:return
        if (state.itemCount < 1) {
            selectedPosition = -1
            removeAndRecycleAllViews(recycler)
            return
        }
        if (selectedPosition < 0) {
            selectedPosition = 0
        }
        detachAndScrapAttachedViews(recycler)
        fillChildren(recycler, state)
    }

    private fun fillChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        val layoutCount = shownCount / 2
        val childHeight = height / shownCount
        for (index in 0 until layoutCount) {
            if (index == 0) {
                val view = recycler.getViewForPosition(selectedPosition)
                addView(view)

            }
        }

    }


    private fun getChildrenScale(relativeIndex: Int): Float {
        return zoomFactor.pow(abs(relativeIndex))
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return  RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

}