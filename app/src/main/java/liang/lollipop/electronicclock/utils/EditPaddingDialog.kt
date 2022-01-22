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
import liang.lollipop.base.lazyBind
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.databinding.DialogPaddingEditBinding
import liang.lollipop.electronicclock.view.PaddingView
import java.text.DecimalFormat

/**
 * 这是一个编辑内补白的对话框
 * @author Lollipop
 */
class EditPaddingDialog private constructor(context: Context) : Dialog(context) {

    companion object {
        fun create(
            context: Context,
            run: (EditPaddingDialog.() -> Unit)? = null
        ): EditPaddingDialog {
            val dialog = EditPaddingDialog(context)
            run?.invoke(dialog)
            return dialog
        }
    }

    private val binding: DialogPaddingEditBinding by lazyBind()

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

    var borderColor = Color.WHITE

    var callback: Callback? = null

    private val decimalFormat = DecimalFormat("0.00")

    private var selectedTint: ColorStateList = ColorStateList.valueOf(selectedIconColor)
    private var defaultTint: ColorStateList = ColorStateList.valueOf(defaultIconColor)

    private val tmpPadding = FloatArray(4)

    fun onPaddingConfirmed(run: (FloatArray) -> Unit): EditPaddingDialog {
        callback = object : Callback {
            override fun onPaddingConfirmed(paddings: FloatArray) {
                run(paddings)
            }
        }
        return this
    }

    fun putPaddingValue(p: FloatArray) {
        putPaddingValue(p[0], p[1], p[2], p[3])
    }

    fun putPaddingValue(left: Float, top: Float, right: Float, bottom: Float) {
        tmpPadding[0] = left
        tmpPadding[1] = top
        tmpPadding[2] = right
        tmpPadding[3] = bottom
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        initView()

        val layoutParams = window?.attributes ?: return
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
        window?.setWindowAnimations(R.style.dialogAnim)
    }

    private fun initView() {
        binding.paddingView.touchWidthDp(touchWidthDp)
        binding.paddingView.pointRadiusDp(pointRadiusDp)
        binding.paddingView.borderWidthDp(borderWidthDp)
        binding.paddingView.color = borderColor

        binding.positiveBtn.setOnClickListener {
            submit()
        }

        binding.negativeBtn.setOnClickListener {
            dismiss()
        }

        binding.paddingView.onDragEndListener = {
            onDragEnd()
        }
        binding.paddingView.onDragStartListener = { target ->
            onDragStart(target)
        }
        binding.paddingView.onDragMoveListener = { target, left, top, right, bottom ->
            onDragMove(target, left, top, right, bottom)
        }
        binding.paddingView.putPaddingValue(
            tmpPadding[0],
            tmpPadding[1],
            tmpPadding[2],
            tmpPadding[3]
        )

        binding.leftInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(
                it,
                1 - (binding.paddingView.paddingRight * 1F / binding.paddingView.width)
            )
            binding.leftInput.setText(value.formatNum())
            binding.paddingView.setPadding(PaddingView.TouchTarget.Left, value)
        }
        binding.topInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(
                it,
                1 - (binding.paddingView.paddingBottom * 1F / binding.paddingView.height)
            )
            binding.topInput.setText(value.formatNum())
            binding.paddingView.setPadding(PaddingView.TouchTarget.Top, value)
        }
        binding.rightInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(
                it,
                1 - (binding.paddingView.paddingLeft * 1F / binding.paddingView.width)
            )
            binding.rightInput.setText(value.formatNum())
            binding.paddingView.setPadding(PaddingView.TouchTarget.Right, value)
        }
        binding.bottomInput.onImeDown {
            it.clearFocus()
            val value = formatValueToFloat(
                it,
                1 - (binding.paddingView.paddingTop * 1F / binding.paddingView.height)
            )
            binding.bottomInput.setText(value.formatNum())
            binding.paddingView.setPadding(PaddingView.TouchTarget.Bottom, value)
        }

        onDragMove(
            PaddingView.TouchTarget.Full,
            tmpPadding[0],
            tmpPadding[1],
            tmpPadding[2],
            tmpPadding[3]
        )
        onDragEnd()
    }

    private fun submit() {
        callback?.let {
            val padding = FloatArray(4)
            padding[0] = binding.paddingView.paddingLeft * 1F / binding.paddingView.width
            padding[1] = binding.paddingView.paddingTop * 1F / binding.paddingView.height
            padding[2] = binding.paddingView.paddingRight * 1F / binding.paddingView.width
            padding[3] = binding.paddingView.paddingBottom * 1F / binding.paddingView.height
            it.onPaddingConfirmed(padding)
        }
        dismiss()
    }

    private fun onDragEnd() {
        binding.leftIcon.imageTintList = defaultTint
        binding.topIcon.imageTintList = defaultTint
        binding.rightIcon.imageTintList = defaultTint
        binding.bottomIcon.imageTintList = defaultTint
    }

    private fun onDragStart(target: PaddingView.TouchTarget) {
        when (target) {
            PaddingView.TouchTarget.Left -> {
                binding.leftIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Top -> {
                binding.topIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Right -> {
                binding.rightIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Bottom -> {
                binding.bottomIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.Full -> {
                binding.leftIcon.imageTintList = selectedTint
                binding.topIcon.imageTintList = selectedTint
                binding.rightIcon.imageTintList = selectedTint
                binding.bottomIcon.imageTintList = selectedTint
            }
            PaddingView.TouchTarget.None -> {
                // do nothing
            }
        }
    }

    private fun onDragMove(
        target: PaddingView.TouchTarget,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        when (target) {
            PaddingView.TouchTarget.Left -> {
                binding.leftInput.setText(left.formatNum())
            }
            PaddingView.TouchTarget.Top -> {
                binding.topInput.setText(top.formatNum())
            }
            PaddingView.TouchTarget.Right -> {
                binding.rightInput.setText(right.formatNum())
            }
            PaddingView.TouchTarget.Bottom -> {
                binding.bottomInput.setText(bottom.formatNum())
            }
            PaddingView.TouchTarget.Full -> {
                binding.leftInput.setText(left.formatNum())
                binding.topInput.setText(top.formatNum())
                binding.rightInput.setText(right.formatNum())
                binding.bottomInput.setText(bottom.formatNum())
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
                        && KeyEvent.ACTION_DOWN == event.action)
            ) {
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