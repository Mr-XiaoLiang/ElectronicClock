package liang.lollipop.electronicclock.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import liang.lollipop.base.WindowInsetsHelper
import liang.lollipop.base.fixInsetsByPadding
import liang.lollipop.base.lazyBind
import liang.lollipop.electronicclock.databinding.ActivityDialogBinding

/**
 * @author lollipop
 * @date 2019-08-13 22:28
 * 底部操作栏的Activity
 */
abstract class DialogActivity : BaseActivity() {

    companion object {
        protected const val TRANSITION_NAME = "DIALOG_BODY"
    }

    private val binding: ActivityDialogBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        transparentSystemUI()
        createContentView()?.let {
            if (it.layoutParams == null) {
                binding.contentGroup.addView(
                    it,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            } else {
                binding.contentGroup.addView(it)
            }
        }
        binding.rootGroup.setOnClickListener {
            if (onBlankPartClick()) {
                onBackPressed()
            }
        }
        binding.rootGroup.fixInsetsByPadding(WindowInsetsHelper.Edge.ALL)
    }

    abstract fun createContentView(): View?

    protected open fun onBlankPartClick(): Boolean {
        return true
    }

    private fun transparentSystemUI() {
        val attributes = window.attributes
        attributes.systemUiVisibility = (
                attributes.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = 0
        window.navigationBarColor = Color.BLACK
    }

}