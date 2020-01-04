package liang.lollipop.electronicclock.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @author lollipop
 * @date 2020-01-01 15:30
 * 可以选择的列表对话框
 */
class CheckListDialog: BottomSheetDialogFragment() {

    private val selectedList = ArrayList<Info>()

    private val unselectedList = ArrayList<Info>()

    private class Adapter(
        private val selectedData: ArrayList<Info>,
        private val unselectedData: ArrayList<Info>,
        selectedTitle: String,
        unselectedTitle: String,
        private val layoutInflater: LayoutInflater
    ): RecyclerView.Adapter<Item>() {

        companion object {
            private const val TYPE_ITEM = 0
            private const val TYPE_TITLE = 1
            private const val TYPE_EMPTY = 2

            private val emptyInfo = Info("", 0)
        }

        private val selectedTitleInfo = Info(selectedTitle, 0)
        private val unselectedTitleInfo = Info(unselectedTitle, 0)

        val selectedTitlePosition = 0

        val unselectedTitlePosition: Int
            get() {
                return if (selectedData.isEmpty()) {
                    2
                } else {
                    selectedData.size + 1
                }
            }

        val emptyInfoPosition: Int
            get() {
                return if (selectedData.isEmpty()) { 1 } else { -1 }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemCount(): Int {
            var size = selectedData.size + unselectedData.size + 2
            // 如果是空的，那么放一个占位的空对象
            if (selectedData.isEmpty()) {
                size += 1
            }
            return size
        }

        override fun getItemViewType(position: Int): Int {
            if (position == selectedTitlePosition || position == unselectedTitlePosition) {
                // 如果是第一个，或者是选中项不为空的时候超过长度选中项的第一个，或者为空时的第三个
                return TYPE_TITLE
            }
            if (position == emptyInfoPosition) {
                // 如果是第二个，并且选中项是空的的时候
                return TYPE_EMPTY
            }
            return TYPE_ITEM
        }

        private fun getInfo(position: Int): Info {
            // 选中项的标题，始终是第一个
            if (position == selectedTitlePosition) {
                return selectedTitleInfo
            }
            // 如果选中项是空的，那么第二个始终是占位项目
            if (position == emptyInfoPosition) {
                return emptyInfo
            }
            // 接着是未选择项目的标题
            if (position == selectedTitlePosition) {
                return unselectedTitleInfo
            }
            return when {
                position < unselectedTitlePosition -> {
                    selectedData[position - 1]
                }
                emptyInfoPosition > 0 -> {
                    unselectedData[position - 3]
                }
                else -> {
                    unselectedData[position - 2]
                }
            }
        }



        override fun onBindViewHolder(holder: Item, position: Int) {
            holder.onBind(getInfo(position))
        }

    }

    private abstract class Item(view: View): RecyclerView.ViewHolder(view) {
        abstract fun onBind(info: Info)
    }

    class Info(val name: String, val id: Int)

}