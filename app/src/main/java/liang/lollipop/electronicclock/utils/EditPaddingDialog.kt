package liang.lollipop.electronicclock.utils

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_padding_edit.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.view.PaddingView
import java.text.DecimalFormat

/**
 * 这是一个编辑内补白的对话框
 * @author Lollipop
 */
class EditPaddingDialog private constructor(context: Context) : Dialog(context) {

    companion object {
        fun create(context: Context, run: (EditPaddingDialog.() -> Unit)? = null) : EditPaddingDialog {
            val dialog = EditPaddingDialog(context)
            run?.invoke(dialog)
            return dialog
        }
    }

    var touchWidthDp = 20F
    var pointRadiusDp = 5F
    var borderWidthDp = 2F

    var selectedIconColor = Color.WHITE
        set(value) {
            field = value
            selectedTint = ColorStateList.valueOf(value)
        }
    var defaultIconColor = Color.WHITE and 0x80FFFFFF.toInt()
        set(value) {
            field = value
            defaultTint = ColorStateList.valueOf(value)
        }

    var callback: Callback? = null

    private val decimalFormat = DecimalFormat("0.00")

    private var selectedTint: ColorStateList = ColorStateList.valueOf(selectedIconColor)
    private var defaultTint: ColorStateList = ColorStateList.valueOf(defaultIconColor)

    private val tmpPadding = FloatArray(4)

    fun putPaddingValue(left: Float, top: Float, right: Float, bottom: Float) {
        tmpPadding[0] = left
        tmpPadding[1] = top
        tmpPadding[2] = right
        tmpPadding[3] = bottom
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_padding_edit)
        initView()

        val layoutParams = window?.attributes ?: return
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
        window?.setWindowAnimations(R.style.dialogAnim)
    }

    private fun initView() {
        paddingView.touchWidthDp(touchWidthDp)
        paddingView.pointRadiusDp(pointRadiusDp)
        paddingView.borderWidthDp(borderWidthDp)

        positiveBtn.setOnClickListener {
            submit()
        }

        negativeBtn.setOnClickListener {
            dismiss()
        }

        paddingView.onDragEndListener = {
            onDragEnd()
        }
        paddingView.onDragStartListener = {target ->
            onDragStart(target)
        }
        paddingView.onDragMoveListener = { target, left, top, right, bottom ->
            onDragMove(target, left, top, right, bottom)
        }
        paddingView.putPaddingValue(tmpPadding[0], tmpPadding[1], tmpPadding[2], tmpPadding[3])

        leftInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(it,
                1 - (paddingView.paddingRight * 1F / paddingView.width))
            leftInput.setText(value.formatNum())
            paddingView.setPadding(PaddingView.TouchTarget.Left, value)
        }
        topInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(it,
                1 - (paddingView.paddingBottom * 1F / paddingView.height))
            topInput.setText(value.formatNum())
            paddingView.setPadding(PaddingView.TouchTarget.Top, value)
        }
        rightInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(it,
                1 - (paddingView.paddingLeft * 1F / paddingView.width))
            rightInput.setText(value.formatNum())
            paddingView.setPadding(PaddingView.TouchTarget.Right, value)
        }
        bottomInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(it,
                1 - (paddingView.paddingTop * 1F / paddingView.height))
            bottomInput.setText(value.formatNum())
            paddingView.setPadding(PaddingView.TouchTarget.Bottom, value)
        }

        onDragEnd()
    }

    private fun submit() {
        callback?.let {
            val padding = FloatArray(4)
            padding[0] = paddingView.paddingLeft * 1F / paddingView.width
            padding[1] = paddingView.paddingTop * 1F / paddingView.height
            padding[2] = paddingView.paddingRight * 1F / paddingView.width
            padding[3] = paddingView.paddingBottom * 1F / paddingView.height
            it.onPaddingConfirmed(padding)
        }
        dismiss()
    }

    private fun onDragEnd() {
        leftIcon.imageTintList = defaultTint
        topIcon.imageTintList = defaultTint
        rightIcon.imageTintList = defaultTint
        bottomIcon.imageTintList = defaultTint
    }

    private fun onDragStart(target: PaddingView.TouchTarget) {
        when (target) {
            PaddingView.TouchTarget.Left -> {
                leftIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Top -> {
                topIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Right -> {
                rightIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Bottom -> {
                bottomIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Full -> {
                leftIcon.imageTintList = selectedTint
                topIcon.imageTintList = selectedTint
                rightIcon.imageTintList = selectedTint
                bottomIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.None -> {
                // do nothing
            }
        }
    }

    private fun onDragMove(target: PaddingView.TouchTarget, left: Float, top: Float, right: Float, bottom: Float) {
        when (target) {
            PaddingView.TouchTarget.Left -> {
                leftInput.setText(left.formatNum())
            }
            PaddingView.TouchTarget.Top -> {
                topInput.setText(top.formatNum())
            }
            PaddingView.TouchTarget.Right -> {
                rightInput.setText(right.formatNum())
            }
            PaddingView.TouchTarget.Bottom -> {
                bottomInput.setText(bottom.formatNum())
            }
            PaddingView.TouchTarget.Full -> {
                leftInput.setText(left.formatNum())
                topInput.setText(top.formatNum())
                rightInput.setText(right.formatNum())
                bottomInput.setText(bottom.formatNum())
            }
            PaddingView.TouchTarget.None -> {
                // do nothing
            }
        }
    }

    private fun Float.formatNum(): String {
        return decimalFormat.format(this * 100)
    }

    private fun TextInputEditText.onImeDown(run: (TextInputEditText) -> Unit) {
        this.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE ||
                (KeyEvent.KEYCODE_ENTER == event?.keyCode
                        && KeyEvent.ACTION_DOWN == event.action)) {
                run(v as TextInputEditText)
            }
            return@setOnEditorActionListener false
        }
    }

    private fun formatValueToFloat(input: TextInputEditText, max: Float): Float {
        return try {
            val strValue = input.text.toString()
            if (strValue.isEmpty()) {
                0F
            } else {
                val value = strValue.toFloat() / 100F
                when {
                    value > max -> max
                    value < 0 -> 0F
                    else -> value
                }
            }
        } catch (e: Exception) {
            0F
        }
    }

    interface Callback {
        fun onPaddingConfirmed(paddings: FloatArray)
    }

}