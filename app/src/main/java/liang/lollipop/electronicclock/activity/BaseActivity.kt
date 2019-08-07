package liang.lollipop.electronicclock.activity

import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import liang.lollipop.electronicclock.fragment.FullScreenFragment

/**
 * @author lollipop
 * @date 2019-08-07 21:43
 * 基类，用于包装一些简单的方法
 */
open class BaseActivity: AppCompatActivity() {

    private var lToolbar: Toolbar? = null

    private val windowInset = Rect()

    protected fun bindToolBar(t: Toolbar) {
        lToolbar = t
        setSupportActionBar(t)
    }

    protected fun initInsetListener(rootView: View) {
        rootView.fitsSystemWindows = true
        rootView.setOnApplyWindowInsetsListener { _, insets ->

            val left = insets.systemWindowInsetLeft
            val top = insets.systemWindowInsetTop
            val right = insets.systemWindowInsetRight
            val bottom = insets.systemWindowInsetBottom

            windowInset.set(left, top, right, bottom)
            whenDisplayCutout(left, top, right)
            onWindowInsetsChange(left, top, right, bottom)
            updateFragmentInset(left, top, right, bottom)
            // return
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

}