package liang.lollipop.electronicclock.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.dialog_number_selected.*
import liang.lollipop.electronicclock.R
import liang.lollipop.widget.utils.dp

/**
 * @author lollipop
 * @date 2019-08-15 22:54
 * 数字选择的dialog
 */
class NumberSelectedDialog(private val builder: Builder): Dialog(builder.context) {

    companion object {
        const val TEXT_COLOR = Color.WHITE

        const val ITEM_WIDTH = ViewGroup.LayoutParams.WRAP_CONTENT
        const val ITEM_HEIGHT = ViewGroup.LayoutParams.MATCH_PARENT

        const val FONT_SIZE = 18F

        fun create(context: Context): Builder {
            return Builder(context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_number_selected)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
        recyclerView.adapter = NumberAdapter(builder.context, builder.min, builder.max, FONT_SIZE) {
            onNumberSelected(builder.min + it.adapterPosition)
        }
        recyclerView.adapter?.notifyDataSetChanged()

        titleView.text = builder.title

        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun onNumberSelected(number: Int) {
        builder.onNumberSelected?.invoke(this, number)
    }

    class Builder(val context: Context) {
        var min = 0
        var max = Int.MAX_VALUE
        var title = ""
        var onNumberSelected: ((dialog: Dialog, number: Int) -> Unit)? = null

        fun show() {
            NumberSelectedDialog(this).show()
        }
    }

    private class NumberAdapter(private val context: Context,
                                private val min: Int,
                                private val max: Int,
                                private val fontSize: Float,
                                private val onClickListener: (RecyclerView.ViewHolder) -> Unit)
        : RecyclerView.Adapter<NumberHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberHolder {
            return NumberHolder.create(context, fontSize, onClickListener)
        }

        override fun getItemCount(): Int {
            return max - min + 1
        }

        override fun onBindViewHolder(holder: NumberHolder, position: Int) {
            holder.onBind(min + position)
        }

    }

    private class NumberHolder(private val textView: TextView,
                               private val onClickListener: (RecyclerView.ViewHolder) -> Unit): RecyclerView.ViewHolder(textView) {

        companion object {
            fun create(context: Context, fontSize: Float, lis: (RecyclerView.ViewHolder) -> Unit): NumberHolder {
                val view = TextView(context).apply {
                    textSize = fontSize
                    gravity = Gravity.CENTER
                    setTextColor(TEXT_COLOR)
                    val paddingSize = context.resources.dp(fontSize).toInt()
                    setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
                    layoutParams = RecyclerView.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT)
                }
                return NumberHolder(view, lis)
            }
        }

        init {
            textView.setOnClickListener{
                onClickListener(this)
            }
        }

        fun onBind(number: Int) {
            textView.text = "$number"
        }

    }

}