package liang.lollipop.electronicclock.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import liang.lollipop.electronicclock.R

/**
 * @author lollipop
 * @date 2019-08-13 22:28
 * 底部操作栏的Activity
 */
open class BottomNavigationActivity: BaseActivity() {

    companion object {
        private const val DEF_LAYOUT_ID = R.layout.activity_bottom_navigation
    }

    protected open val contentViewId = 0

    protected open val floatingViewId = 0

    protected open val layoutId = DEF_LAYOUT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layoutId != 0) {
            setContentView(layoutId)
            if (layoutId == DEF_LAYOUT_ID) {
                transparentSystemUI()
                initInsetListener(rootGroup)
                bindToolBar(appBarLayout)
                fab.hide()
                if (contentViewId != 0) {
                    layoutInflater.inflate(contentViewId, contentGroup, true)
                }
            }
        }
    }

    override fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        super.onWindowInsetsChange(left, top, right, bottom)
        if (layoutId == DEF_LAYOUT_ID) {
            rootGroup.setPadding(left, top, right, 0)
        }
    }

    protected fun showFAB(icon: Int, run: ((FloatingActionButton) -> Unit)? = null) {
        fab.setImageResource(icon)
        fab.show()
        run?.invoke(fab)
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
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = 0
        window.navigationBarColor = Color.BLACK
    }

}