package liang.lollipop.electronicclock.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_seekbar.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.view.AutoSeekBar

/**
 * 一个滑动条的Dialog
 * @author Lollipop
 * @date 2019/09/03
 */
class SeekBarDialog private constructor(context: Context): Dialog(context), AutoSeekBar.OnProgressChangeListener {

    companion object {

        fun getInstance(context: Context, run: SeekBarDialog.() -> Unit): SeekBarDialog {
            return SeekBarDialog(context).apply(run)
        }
    }

    var title = ""
    var value = 0
    var max = 100
    var min = 0

    private var onProgressConfirmListener: OnProgressConfirmListener? = null

    fun onProgressConfirm(run: (value: Int) -> Unit): SeekBarDialog {
        onProgressConfirmListener = object : OnProgressConfirmListener {
            override fun onProgressConfirm(value: Int) {
                run(value)
            }
        }
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_seekbar)
        initView()

        val layoutParams = window?.attributes?:return
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
        window?.setWindowAnimations(R.style.dialogAnim)
    }

    private fun initView() {
        titleView.text = title
        seekBar.max = max.toFloat()
        seekBar.min = min.toFloat()
        seekBar.onProgressChangeListener = this
        seekBar.progress = value.toFloat()

        negativeBtn.setOnClickListener {
            dismiss()
        }
        positiveBtn.setOnClickListener {
            onProgressConfirmListener?.onProgressConfirm(value)
            dismiss()
        }
    }

    override fun onProgressChange(view: AutoSeekBar, progress: Float) {
        value = progress.toInt()
        valueView.text = "$value"
    }

    interface OnProgressConfirmListener {
        fun onProgressConfirm(value: Int)
    }

}