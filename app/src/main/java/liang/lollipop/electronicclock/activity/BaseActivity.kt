package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import liang.lollipop.electronicclock.utils.BroadcastHelper

/**
 * @author lollipop
 * @date 2019-08-07 21:43
 * 基类，用于包装一些简单的方法
 */
open class BaseActivity : AppCompatActivity() {

    private val windowInset = Rect()

    protected var isShowBack = true

    protected var isFullScreen = false
        private set

    protected var isPaddingToolbarWithInset = true

    protected val broadcastHelper: BroadcastHelper by lazy {
        BroadcastHelper.create { action, intent ->
            onReceive(action, intent)
        }
    }

    protected fun bindToolBar(t: Toolbar) {
        setSupportActionBar(t)
    }

    protected fun fullScreen() {
        isFullScreen = true
        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = this.window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            this.window.attributes = lp
        }
        hideSystemUI()
    }

    protected open fun onReceive(action: String, intent: Intent) {}

    override fun onResume() {
        super.onResume()
        supportActionBar?.setDisplayHomeAsUpEnabled(isShowBack)
        if (isFullScreen) {
            hideSystemUI()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun hideSystemUI() {
        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun alert(run: AlertDialog.Builder.() -> Unit) {
        run(AlertDialog.Builder(this))
    }

}