package liang.lollipop.electronicclock.list.base

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * 用来做RecyclerView滑动删除和拖拽排序
 *
 */
class LItemTouchHelper(private val callback: LItemTouchCallback) : ItemTouchHelper(callback) {

    companion object {

        fun bindTo(
            recyclerView: RecyclerView,
            onItemTouchCallbackListener: LItemTouchCallback.OnItemTouchCallbackListener,
            initRun: (LItemTouchHelper.() -> Unit)? = null
        ): LItemTouchHelper {
            val helper = LItemTouchHelper(LItemTouchCallback(onItemTouchCallbackListener)).apply {
                attachToRecyclerView(recyclerView)
                canDrag = false
                canSwipe = false
            }
            initRun?.invoke(helper)
            return helper
        }

    }

    var canDrag: Boolean
        set(value) {
            callback.isCanDrag = value
        }
        get() {
            return callback.isCanDrag
        }

    var canSwipe: Boolean
        set(value) {
            callback.isCanSwipe = value
        }
        get() {
            return callback.isCanSwipe
        }

    fun onSwiped(holder: RecyclerView.ViewHolder) {
        callback.onSwiped(holder, 0)
    }

    fun setStateChangedListener(stateChangedListener: LItemTouchCallback.OnItemTouchStateChangedListener) {
        this.callback.setStateChangedListener(stateChangedListener)
    }

}