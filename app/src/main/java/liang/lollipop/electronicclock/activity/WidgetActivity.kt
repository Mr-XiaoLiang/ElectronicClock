package liang.lollipop.electronicclock.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_widget.*
import liang.lollipop.electronicclock.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget)
        initInsetListener(rootGroup)
        initView()
        initData()
    }

    private fun initView() {
        widgetGroup.lockedGrid = true
        widgetHelper = WidgetHelper.with(this, widgetGroup).let {
            it.dragStrokeWidth = resources.dp(20F)
            it.selectedBorderWidth = resources.dp(2F)
            it.touchPointRadius = resources.dp(5F)
            it.selectedColor = ContextCompat.getColor(this, R.color.colorPrimary)
            it.focusColor = ContextCompat.getColor(this, R.color.colorAccent)
            it.pendingLayoutTime = 800L
            it.isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            it.canDrag = false
            it
        }.onCantLayout {
            for (panel in it) {
                widgetHelper.removePanel(panel)
            }
        }
    }

    private fun initData() {
        widgetHelper.updateByDB { status ->
            if (status == WidgetHelper.LoadStatus.START) {
                loadingView.show()
            } else {
                loadingView.hide()
            }
            if (widgetHelper.panelCount < 1) {
                onWidgetEmpty()
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
    }

    override fun onStop() {
        super.onStop()
        widgetHelper.onStop()
    }

    override fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        super.onWindowInsetsChange(left, top, right, bottom)
        logger("onWindowInsetsChange($left, $top, $right, $bottom)")
        widgetGroup.setPadding(left, top, right, bottom)
    }

}
