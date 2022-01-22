package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.WindowManager
import liang.lollipop.base.WindowInsetsHelper
import liang.lollipop.base.fixInsetsByPadding
import liang.lollipop.base.lazyBind
import liang.lollipop.electronicclock.databinding.ActivityWidgetBinding
import liang.lollipop.electronicclock.utils.BroadcastHelper
import liang.lollipop.electronicclock.utils.PreferenceHelper
import liang.lollipop.electronicclock.utils.clockOrientation
import liang.lollipop.electronicclock.utils.gridSize
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.info.ClockPanelInfo

/**
 * 小部件展示的页面
 * @author Lollipop
 */
class WidgetActivity : BaseActivity() {

    private val binding: ActivityWidgetBinding by lazyBind()

    private lateinit var widgetHelper: WidgetHelper

    private var orientationEventListener: OrientationEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScreenOrientation()
        fullScreen()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(binding.root)
        initView()
        initData()

        broadcastHelper.addActions(BroadcastHelper.ACTION_WIDGET_INFO_CHANGE)
        broadcastHelper.register(this)

        orientationEventListener = object : OrientationEventListener(this) {
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
            else -> {}
        }
    }

    private fun initView() {
        binding.widgetGroup.lockedGrid = true
        binding.widgetGroup.gridCount = this.gridSize
        widgetHelper = PreferenceHelper.createWidgetHelper(this, binding.widgetGroup)
            .onCantLayout {
                for (panel in it) {
                    widgetHelper.removePanel(panel)
                }
            }
        widgetHelper.isInEditMode = false
        binding.widgetGroup.fixInsetsByPadding(WindowInsetsHelper.Edge.ALL)
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
                binding.loadingView.show()
            } else {
                binding.loadingView.hide()
                if (widgetHelper.panelCount < 1) {
                    onWidgetEmpty()
                }
            }
        }
    }

    private fun onWidgetEmpty() {
        binding.widgetGroup.post {
            val clockPanelInfo = ClockPanelInfo()
            clockPanelInfo.sizeChange(
                binding.widgetGroup.spanCountX,
                binding.widgetGroup.spanCountY
            )
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

}
