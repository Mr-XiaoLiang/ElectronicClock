package liang.lollipop.electronicclock.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_list_select.*
import liang.lollipop.electronicclock.R
import liang.lollipop.widget.utils.dp

/**
 * @author lollipop
 * @date 2019-10-10 22:26
 * 列表选择的对话框
 */
class ListSelectDialog (private val builder: Builder) : Dialog(builder.context)  {

    companion object {
        const val TEXT_COLOR = Color.WHITE

        const val ITEM_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT
        const val ITEM_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT

        const val FONT_SIZE = 18F

        fun create(context: Context): Builder {
            return Builder(context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_list_select)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = ListAdapter(builder.dataList, builder.context, FONT_SIZE) {
            onItemSelected(it.adapterPosition)
        }
        recyclerView.adapter?.notifyDataSetChanged()

        titleView.text = builder.title

        val layoutParams = window?.attributes ?: return
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
        window?.setWindowAnimations(R.style.dialogAnim)
    }

    private fun onItemSelected(position: Int) {
        builder.onItemSelectedListener?.invoke(this, position, builder.dataList[position])
    }

    private class ListAdapter(private val data: ArrayList<String>,
                              private val context: Context,
                              private val fontSize: Float,
                              private val onClickListener: (RecyclerView.ViewHolder) -> Unit) : RecyclerView.Adapter<ListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
            return ListHolder.create(context, fontSize, onClickListener)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ListHolder, position: Int) {
            holder.onBind(data[position])
        }

    }

    private class ListHolder(private val textView: TextView,
                               private val onClickListener: (RecyclerView.ViewHolder) -> Unit):
        RecyclerView.ViewHolder(textView) {

        companion object {
            fun create(context: Context, fontSize: Float,
                       lis: (RecyclerView.ViewHolder) -> Unit): ListHolder {
                val view = TextView(context).apply {
                    textSize = fontSize
                    gravity = Gravity.CENTER_VERTICAL
                    setTextColor(TEXT_COLOR)
                    val paddingSize = context.resources.dp(fontSize).toInt()
                    setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
                    layoutParams = RecyclerView.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT)
                }
                return ListHolder(view, lis)
            }
        }

        init {
            textView.setOnClickListener{
                onClickListener(this)
            }
        }

        fun onBind(value: String) {
            textView.text = value
        }

    }

    class Builder(val context: Context) {
        val dataList = ArrayList<String>()
        var selected = -1
        var title = ""
        var onItemSelectedListener: ((dialog: Dialog, index: Int, value: String) -> Unit)? = null

        fun setData(data: ArrayList<String>): Builder {
            dataList.clear()
            dataList.addAll(data)
            return this
        }

        fun selectedTo(index: Int): Builder {
            this.selected = index
            return this
        }

        fun onItemSelected(listener: ((dialog: Dialog, index: Int, value: String) -> Unit)? = null): Builder {
            this.onItemSelectedListener = listener
            return this
        }

        fun show() {
            ListSelectDialog(this).show()
        }
    }

}