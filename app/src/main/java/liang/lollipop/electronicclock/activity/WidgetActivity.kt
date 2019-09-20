package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.WindowManager
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_widget.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.*
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.utils.dp

/**
 * 小部件展示的页面
 * @author Lollipop
 */
class WidgetActivity : BaseActivity() {

    private lateinit var widgetHelper: WidgetHelper

    private val logger = Utils.loggerI("WidgetActivity")

    private var orientationEventListener: OrientationEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScreenOrientation()
        fullScreen()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_widget)
        initInsetListener(rootGroup)
        initView()
        initData()

        broadcastHelper.addActions(BroadcastHelper.ACTION_WIDGET_INFO_CHANGE)
        broadcastHelper.register(this)

        orientationEventListener = object: OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                fullScreen()
            }
        }
    }

    override fun onReceive(action: String, intent: Intent) {
        super.onReceive(action, intent)
        when (action) {
            BroadcastHelper.ACTION_WIDGET_INFO_CHANGE -> {
                initData()
            }
            else -> { }
        }
    }

    private fun initView() {
        widgetGroup.lockedGrid = true
        widgetGroup.gridCount = this.gridSize
        widgetHelper = PreferenceHelper.createWidgetHelper(this, widgetGroup)
            .onCantLayout {
            for (panel in it) {
                widgetHelper.removePanel(panel)
            }
        }
    }

    private fun setScreenOrientation() {
        requestedOrientation = when (this.clockOrientation) {
            PreferenceHelper.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            PreferenceHelper.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }

    private fun initData() {
        widgetHelper.updateByDB { status ->
            if (status == WidgetHelper.LoadStatus.START) {
                loadingView.show()
            } else {
                loadingView.hide()
                if (widgetHelper.panelCount < 1) {
                    onWidgetEmpty()
                }
            }
        }
    }

    private fun onWidgetEmpty() {
        widgetGroup.post {
            val clockPanelInfo = ClockPanelInfo()
            clockPanelInfo.sizeChange(widgetGroup.spanCountX, widgetGroup.spanCountY)
            clockPanelInfo.offset(0, 0)
            widgetHelper.addPanel(clockPanelInfo)
        }
    }

    override fun onStart() {
        super.onStart()
        widgetHelper.onStart()
        orientationEventListener?.enable()
    }

    override fun onStop() {
        super.onStop()
        widgetHelper.onStop()
        orientationEventListener?.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        broadcastHelper.unregister(this)
    }

    override fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        super.onWindowInsetsChange(left, top, right, bottom)
        logger("onWindowInsetsChange($left, $top, $right, $bottom)")
        widgetGroup.setPadding(left, top, right, bottom)
    }

}
