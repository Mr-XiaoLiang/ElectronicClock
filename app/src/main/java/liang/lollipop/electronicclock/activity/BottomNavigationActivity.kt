package liang.lollipop.electronicclock.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import liang.lollipop.base.WindowInsetsHelper
import liang.lollipop.base.fixInsetsByPadding
import liang.lollipop.base.lazyBind
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.databinding.ActivityBottomNavigationBinding

/**
 * @author lollipop
 * @date 2019-08-13 22:28
 * 底部操作栏的Activity
 */
abstract class BottomNavigationActivity : BaseActivity() {

    private val binding: ActivityBottomNavigationBinding by lazyBind()

    protected val fab: FloatingActionButton
        get() {
            return binding.fab
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        transparentSystemUI()
        bindToolBar(binding.appBarLayout)
        binding.fab.hide()
        binding.contentGroup.post {
            binding.contentGroup.setPadding(
                0,
                0,
                0,
                binding.appBarLayout.height
            )
        }
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
        binding.contentLoading.putColor(
            Color.WHITE,
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorAccent)
        )
        binding.rootGroup.fixInsetsByPadding(WindowInsetsHelper.Edge.HEADER)
    }

    abstract fun createContentView(): View?

    protected fun showFAB(icon: Int, run: ((FloatingActionButton) -> Unit)? = null) {
        binding.fab.setImageResource(icon)
        binding.fab.show()
        run?.invoke(binding.fab)
    }

    protected fun Snackbar.avoidWeight(): Snackbar {
        if (binding.fab.isShown) {
            this.anchorView = binding.fab
        } else {
            this.anchorView = binding.appBarLayout
        }
        return this
    }

    protected fun startContentLoading() {
        binding.contentLoading.show()
    }

    protected fun stopContentLoading() {
        binding.contentLoading.hide()
    }

    private fun transparentSystemUI() {
        WindowInsetsHelper.initWindowFlag(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = 0
        window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBarColor)
    }

}