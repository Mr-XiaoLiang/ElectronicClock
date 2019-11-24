package liang.lollipop.electronicclock

import android.app.Application
import android.graphics.Color
import androidx.core.content.ContextCompat
import liang.lollipop.electronicclock.utils.LauncherHelper
import liang.lollipop.guidelinesview.Guidelines

/**
 * @author lollipop
 * @date 2019-08-17 23:45
 * 应用上下文
 */
class LApplication: Application() {

    private val launcherHelper = LauncherHelper()

    override fun onCreate() {
        super.onCreate()
        Guidelines.global {
            fontColor = Color.WHITE
            fontSize = 18F
            panelColor = ContextCompat.getColor(this@LApplication, R.color.colorPrimary)
            backgroundColor = changeAlpha(Color.BLACK, 200)
        }
        launcherHelper.registerReceiver(this)
    }

}