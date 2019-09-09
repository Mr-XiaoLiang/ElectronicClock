package liang.lollipop.electronicclock.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import liang.lollipop.electronicclock.R

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

    }

}