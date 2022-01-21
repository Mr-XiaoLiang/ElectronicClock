package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import liang.lollipop.electronicclock.fragment.FullScreenFragment
import liang.lollipop.electronicclock.utils.BroadcastHelper

/**
 * @author lollipop
 * @date 2019-08-07 21:43
 * 基类，用于包装一些简单的方法
 */
open class BaseActivity: AppCompatActivity() {

    private var lToolbar: Toolbar? = null

    private val windowInset = Rect()

    protected var isShowBack = true

    protected var isFullScreen = false
        private set

    protected var isPaddingToolbarWithInset = true

    protected val broadcastHelper: BroadcastHelper by lazy {
        BroadcastHelper.create{ action, intent ->
            onReceive(action, intent)
        }
    }

    protected fun bindToolBar(t: Toolbar) {
        lToolbar = t
        setSupportActionBar(t)
    }

    protected fun fullScreen() {
        isFullScreen = true
        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = this.window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            this.window.attributes = lp
        }
        hideSystemUI()
    }

    protected open fun onReceive(action: String, intent: Intent) {}

    override fun onResume() {
        super.onResume()
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(isShowBack)
        }
        if (isFullScreen) {
            hideSystemUI()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    protected fun initInsetListener(rootView: View) {
        insetListener(rootView) {insets ->
            val left = insets.systemWindowInsetLeft
            val top = insets.systemWindowInsetTop
            val right = insets.systemWindowInsetRight
            val bottom = insets.systemWindowInsetBottom

            windowInset.set(left, top, right, bottom)
            whenDisplayCutout(left, top, right)
            onWindowInsetsChange(left, top, right, bottom)
            updateFragmentInset(left, top, right, bottom)
        }
    }

    protected fun insetListener(group: View, onInsetChange: (insets: WindowInsets) -> Unit) {
        group.fitsSystemWindows = true
        group.setOnApplyWindowInsetsListener { _, insets ->
            onInsetChange(insets)
            insets.consumeStableInsets()
        }
    }

    private fun updateFragmentInset(left: Int, top: Int, right: Int, bottom: Int) {
        val fragments = supportFragmentManager.fragments
        fragments.forEach {
            if (it.isVisible && it is FullScreenFragment) {
                it.onWindowInsetsChange(left, top, right, bottom)
            }
        }
    }

    private fun whenDisplayCutout(leftInset: Int, statusBarHeight: Int, rightInset: Int) {
        if (!isPaddingToolbarWithInset) {
            return
        }
        val toolBar = lToolbar?:return
        when(val layoutParams = toolBar.layoutParams?:return) {
            is CoordinatorLayout.LayoutParams -> {
                layoutParams.topMargin = statusBarHeight
                layoutParams.leftMargin = leftInset
                layoutParams.rightMargin = rightInset
                toolBar.layoutParams = layoutParams
            }
            is CollapsingToolbarLayout.LayoutParams -> {
                layoutParams.topMargin = statusBarHeight
                layoutParams.leftMargin = leftInset
                layoutParams.rightMargin = rightInset
                toolBar.layoutParams = layoutParams
            }
            is AppBarLayout.LayoutParams -> {
                layoutParams.topMargin = statusBarHeight
                layoutParams.leftMargin = leftInset
                layoutParams.rightMargin = rightInset
                toolBar.layoutParams = layoutParams
            }
            is FrameLayout.LayoutParams -> {
                layoutParams.topMargin = statusBarHeight
                layoutParams.leftMargin = leftInset
                layoutParams.rightMargin = rightInset
                toolBar.layoutParams = layoutParams
            }
            is LinearLayout.LayoutParams -> {
                layoutParams.topMargin = statusBarHeight
                layoutParams.leftMargin = leftInset
                layoutParams.rightMargin = rightInset
                toolBar.layoutParams = layoutParams
            }
        }
    }

    open fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {}

    fun alert(run: AlertDialog.Builder.() -> Unit) {
        run(AlertDialog.Builder(this))
    }

}