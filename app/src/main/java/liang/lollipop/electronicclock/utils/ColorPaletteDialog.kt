package liang.lollipop.electronicclock.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_color_palette.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.list.base.LItemTouchCallback
import liang.lollipop.electronicclock.list.base.LItemTouchHelper
import liang.lollipop.electronicclock.view.HuePaletteView
import liang.lollipop.electronicclock.view.SatValPaletteView
import liang.lollipop.electronicclock.view.TransparencyPaletteView
import liang.lollipop.guidelinesview.util.lifecycleBinding
import liang.lollipop.guidelinesview.util.onCancel
import liang.lollipop.guidelinesview.util.onEnd
import liang.lollipop.guidelinesview.util.onStart
import java.util.*
import kotlin.collections.ArrayList

/**
 * 调色板的对话框
 * @author Lollipop
 */
class ColorPaletteDialog private constructor(context: Context) : Dialog(context),
    HuePaletteView.HueCallback, SatValPaletteView.HSVCallback,
    TransparencyPaletteView.TransparencyCallback,
    LItemTouchCallback.OnItemTouchCallbackListener {

    companion object {
        fun create(context: Context, run: ColorPaletteDialog.() -> Unit): ColorPaletteDialog {
            return ColorPaletteDialog(context).apply(run)
        }

        private const val ERROR_DURATION = 3000L
    }

    private val colorArray = ArrayList<Int>()
    private val hsvTemp = FloatArray(3)

    private var colorRGB = Color.RED

    private var colorAlpha = 255

    private var colorAdapter: ColorAdapter? = null

    var minSize = -1

    var maxSize = -1

    var callback: Callback? = null

    fun putColors(vararg colors: Int) {
        colorArray.clear()
        colors.forEach { colorArray.add(it) }
        colorAdapter?.notifyDataSetChanged()
    }

    fun putColors(colors: ArrayList<Int>) {
        colorArray.clear()
        colorArray.addAll(colors)
        colorAdapter?.notifyDataSetChanged()
    }

    fun onColorConfirmed(run: (colorArray: ArrayList<Int>) -> Unit): ColorPaletteDialog {
        callback = object : Callback {
            override fun onColorConfirmed(colorArray: ArrayList<Int>) {
                run(colorArray)
            }
        }
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_color_palette)
        initView()

        val layoutParams = window?.attributes ?: return
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
        window?.setWindowAnimations(R.style.dialogAnim)
    }

    private fun initView() {
        huePalette.hueCallback = this
        satValPalette.hsvCallback = this
        transparencyPalette.transparencyCallback = this

        addBtn.setOnClickListener {
            addColorToList()
        }

        positiveBtn.setOnClickListener {
            submit()
        }

        negativeBtn.setOnClickListener {
            dismiss()
        }

        colorListView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        colorAdapter = ColorAdapter(colorArray, LayoutInflater.from(context)) { colorHolder ->
            onColorDeleteBtnClick(colorHolder)
        }
        colorListView.adapter = colorAdapter
        colorAdapter?.notifyDataSetChanged()

        LItemTouchHelper.bindTo(colorListView, this) { canDrag = true }

        val initColor = if (colorArray.isEmpty()) {
            Color.RED
        } else {
            colorArray[colorArray.size - 1]
        }
        selected(initColor)
    }

    private fun submit() {
        if (maxSize > 0 && colorArray.size > maxSize) {
            showError(R.string.err_color_too_more)
            return
        }
        if (minSize > 0 && colorArray.size < minSize) {
            showError(R.string.err_color_too_low)
            return
        }
        callback?.onColorConfirmed(colorArray)
        dismiss()
    }

    private fun addColorToList() {
        if (maxSize > 0 && colorArray.size >= maxSize) {
            showError(R.string.err_color_cant_be_more)
        }
        val color = merge()
        colorArray.add(color)
        colorAdapter?.notifyItemInserted(colorArray.size - 1)
    }

    private fun onColorDeleteBtnClick(colorHolder: ColorHolder) {
        if (minSize > 0 && colorArray.size <= minSize) {
            showError(R.string.err_color_cant_be_low)
        }
        val index = colorHolder.adapterPosition
        colorArray.removeAt(index)
        colorAdapter?.notifyItemRemoved(index)
    }

    private fun showError(value: Int) {
        errorView.animate().cancel()
        errorView.alpha = 1F
        errorView.setText(value)
        errorView.animate()
            .alpha(0F)
            .setDuration(ERROR_DURATION)
            .lifecycleBinding {
                onEnd {
                    errorView.text = ""
                    removeThis(it)
                }
                onCancel {
                    removeThis(it)
                }
            }.start()
    }

    override fun onSwiped(adapterPosition: Int) {
        // nothing
    }

    override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
        Collections.swap(colorArray, srcPosition, targetPosition)
        colorAdapter?.notifyItemMoved(srcPosition, targetPosition)
        return true
    }

    private fun selected(color: Int) {
        Color.colorToHSV(color, hsvTemp)
        huePalette.parser(hsvTemp[0])
        satValPalette.parser(hsvTemp[1], hsvTemp[2])
        transparencyPalette.parser(Color.alpha(color))
    }

    override fun onHueSelect(hue: Int) {
        satValPalette.onHueChange(hue.toFloat())
    }

    override fun onHSVSelect(hsv: FloatArray, rgb: Int) {
        colorRGB = rgb
        onColorChange()
    }

    override fun onTransparencySelect(alphaF: Float, alphaI: Int) {
        colorAlpha = alphaI
        onColorChange()
    }

    private fun onColorChange() {
        val color = merge()
        selectedColorView.setStatusColor(color)
        colorValueView.text = color.colorValue()
    }

    private fun merge(alpha: Int = colorAlpha, rgb: Int = colorRGB): Int {
        val a = alpha.range(0, 255) shl 24
        return rgb and 0xFFFFFF or a
    }

    private fun Int.range(min: Int, max: Int): Int {
        if (this < min) {
            return min
        }
        if (this > max) {
            return max
        }
        return this
    }

    private fun Int.colorValue(): String {
        var result = "#"
        result += Color.alpha(this).toHexString()
        result += Color.red(this).toHexString()
        result += Color.green(this).toHexString()
        result += Color.blue(this).toHexString()
        return result
    }

    private fun Int.toHexString(digit: Int = 2): String {
        val builder = StringBuilder(Integer.toHexString(this))
        while (builder.length < digit) {
            builder.insert(0, "0")
        }
        return builder.toString().toUpperCase(Locale.US)
    }

    private class ColorAdapter(
        private val data: ArrayList<Int>,
        private val inflater: LayoutInflater,
        private val onDelete: (ColorHolder) -> Unit
    ) : RecyclerView.Adapter<ColorHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
            return ColorHolder(inflater.inflate(R.layout.item_color, parent, false)).apply {
                onDeleteBtnClickListener = onDelete
            }
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: ColorHolder, position: Int) {
            holder.onBind(data[position])
        }
    }

    private class ColorHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val colorView: View = view.findViewById(R.id.colorView)
        private val deleteView: View = view.findViewById(R.id.deleteBtn)

        companion object {
            private const val DELETE_DURATION = 3 * 1000L
        }

        init {
            view.setOnClickListener(this)
            deleteView.setOnClickListener(this)
        }

        var onDeleteBtnClickListener: ((ColorHolder) -> Unit)? = null

        fun onBind(color: Int) {
            colorView.setBackgroundColor(color)
            deleteView.visibility = View.INVISIBLE
            deleteView.alpha = 1F
            deleteView.animate().cancel()
        }

        override fun onClick(v: View?) {
            when (v) {
                itemView -> {
                    deleteView.animate().cancel()
                    deleteView.alpha = 1F
                    deleteView.animate()
                        .alpha(0F)
                        .setDuration(DELETE_DURATION)
                        .lifecycleBinding {
                            onStart {
                                deleteView.visibility = View.VISIBLE
                            }
                            onEnd {
                                deleteView.visibility = View.INVISIBLE
                                removeThis(it)
                            }
                            onCancel {
                                removeThis(it)
                            }
                        }.start()
                }
                deleteView -> {
                    onDeleteBtnClickListener?.invoke(this)
                }
            }
        }

    }

    interface Callback {
        fun onColorConfirmed(colorArray: ArrayList<Int>)
    }

}