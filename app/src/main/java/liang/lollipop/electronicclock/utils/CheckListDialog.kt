package liang.lollipop.electronicclock.utils

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_check_list.*
import liang.lollipop.electronicclock.R

/**
 * @author lollipop
 * @date 2020-01-01 15:30
 * 可以选择的列表对话框
 */
class CheckListDialog private constructor(private val selectedList: ArrayList<Info>,
                      private val unselectedList: ArrayList<Info>,
                      private val maxSize: Int,
                      private val onCheckedListener: (ArrayList<Info>) -> Unit): BottomSheetDialogFragment() {

    companion object {
        fun show(selected: ArrayList<Info>,
                 unselected: ArrayList<Info>,
                 maxSize: Int,
                 fragmentManager: FragmentManager,
                 tag: String = "CheckListDialog",
                 onChecked: (ArrayList<Info>) -> Unit) {
            // 构造新的对象，以此来避免内部数据操作与外部的直接干涉
            // 但是数据info不做额外的处理，因为本身是final的，不会产生中途的修改
            val selectedData = ArrayList<Info>()
            if (selected == unselected) {
                selectedData.addAll(selected)
            }
            val unselectedData = ArrayList<Info>()
            unselectedData.addAll(unselected)
            CheckListDialog(selectedData, unselectedData, maxSize, onChecked).show(fragmentManager, tag)
        }
    }

    private val floatingTitleListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                val adapter = recyclerView.adapter as Adapter
                val lastItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (lastItem == adapter.unselectedTitlePosition) {
                    val top = layoutManager.findViewByPosition(lastItem)?.top?:0
                    val height = floatTitle.height * 1F
                    floatTitle.translationY = if (top > height) { 0F } else { top - height }
                    floatTitle.findViewById<TextView>(R.id.titleView)
                        .setText(R.string.title_check_list_selected)
                } else {
                    floatTitle.translationY = 0F
                    floatTitle.findViewById<TextView>(R.id.titleView)
                        .setText(if (lastItem < adapter.unselectedTitlePosition) {
                            R.string.title_check_list_selected
                        } else {
                            R.string.title_check_list_unselected
                        })
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_check_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = Adapter(selectedList, unselectedList,
            getString(R.string.title_check_list_selected),
            getString(R.string.title_check_list_unselected), maxSize, layoutInflater)
        recyclerView.addOnScrollListener(floatingTitleListener)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDetach() {
        // 断开时返回结果
        onCheckedListener(selectedList)
        super.onDetach()
    }

    private class Adapter(
        private val selectedData: ArrayList<Info>,
        private val unselectedData: ArrayList<Info>,
        selectedTitle: String,
        unselectedTitle: String,
        private val maxSelectedSize: Int,
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
            return when(viewType) {
                TYPE_EMPTY -> EmptyItem.create(layoutInflater, parent)
                TYPE_TITLE -> TitleItem.create(layoutInflater, parent)
                else -> DefaultItem.create(layoutInflater, parent).onItemClick {
                    onInfoSelected(getInfo(it))
                }
            }
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
            if (position == unselectedTitlePosition) {
                return unselectedTitleInfo
            }
            if (position < unselectedTitlePosition) {
                return selectedData[position - 1]
            }
            return if (emptyInfoPosition > 0) {
                unselectedData[position - selectedData.size - 3]
            } else {
                unselectedData[position - selectedData.size - 2]
            }
        }

        private fun onInfoSelected(info: Info) {
            val isAdd = !selectedData.contains(info)
            var to: Int
            var from: Int
            if (isAdd) {
                // 如果原来是空的，那么移除占位对象
                if (selectedData.isEmpty()) {
                    notifyItemRemoved(1)
                }
                // 追加到末尾
                selectedData.add(info)
                // 由于有title的存在，因此新添加的位置为selectedData.size - 1 + 1
                to = selectedData.size
                // 内部的位置，叠加未选择标题的位置
                from = unselectedData.indexOf(info) + unselectedTitlePosition
                unselectedData.remove(info)
            } else {
                // 本身的位置，叠加标题的位置
                from = selectedData.indexOf(info) + 1
                selectedData.remove(info)
                // 移动到未选中集合中时，始终放在第一个，动画会更好看，并且计算会更加方便
                unselectedData.add(0, info)
                to = unselectedTitlePosition + 1
            }
            if (selectedData.isEmpty()) {
                to -= 1
            }
            // 执行移动动画
            notifyItemMoved(from, to)
            if (selectedData.isEmpty()) {
                // 如果为空，那么增加一个占位item
                notifyItemInserted(1)
                // 如果选中项已经空了，那么不可能触发超出容量的逻辑了
                return
            }
            if (maxSelectedSize > 0 && selectedData.size > maxSelectedSize) {
                // 如果超过最大的大小了，那么移除他
                val old = selectedData.removeAt(0)
                // 始终移除第一个，因此移除动画的开始位置始终是1
                from = 1
                unselectedData.add(0, old)
                // 目的地的位置始终是未选中集合的第一个
                to = unselectedTitlePosition + 1
                // 再次执行移动动画
                notifyItemMoved(from, to)
            }
        }

        override fun onBindViewHolder(holder: Item, position: Int) {
            holder.onBind(getInfo(position))
        }

    }

    private open class Item(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private var onItemClickListener: ((Int) -> Unit)? = null

        fun onItemClick(lis: (Int) -> Unit): Item {
            itemView.setOnClickListener(this)
            onItemClickListener = lis
            return this
        }

        open fun onBind(info: Info) {
        }

        override fun onClick(v: View?) {
            if (v == itemView) {
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }

    private class EmptyItem private constructor(view: View): Item(view) {

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): EmptyItem {
                return EmptyItem(
                    layoutInflater.inflate(
                        R.layout.item_check_list, parent, false))
            }
        }

        init {
            view.findViewById<TextView>(R.id.titleView).apply {
                gravity = Gravity.CENTER
                setText(R.string.empty)
                alpha = 0.6F
            }
        }
    }

    private class DefaultItem private constructor(view: View): Item(view) {

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): DefaultItem {
                return DefaultItem(
                    layoutInflater.inflate(
                        R.layout.item_check_list, parent, false))
            }
        }

        val titleView: TextView = view.findViewById(R.id.titleView)

        override fun onBind(info: Info) {
            super.onBind(info)
            titleView.text = info.name
        }
    }

    private class TitleItem private constructor(view: View): Item(view) {

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): TitleItem {
                return TitleItem(
                    layoutInflater.inflate(
                        R.layout.item_check_list_title, parent, false))
            }
        }

        val titleView: TextView = view.findViewById(R.id.titleView)

        override fun onBind(info: Info) {
            super.onBind(info)
            titleView.text = info.name
        }
    }

    data class Info(val name: String, val id: Int) {
        override fun equals(other: Any?): Boolean {
            other?:return false
            if (other is Info) {
                return this.id == other.id
            }
            return false
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + id
            return result
        }
    }

}