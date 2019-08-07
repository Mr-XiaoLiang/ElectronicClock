package liang.lollipop.electronicclock.activity

import android.os.Bundle
import android.widget.Toast
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
    }

    private fun initView() {
        widgetHelper = WidgetHelper.with(this, widgetGroup).let {
            it.dragStrokeWidth = resources.dp(20F)
            it.selectedBorderWidth = resources.dp(2F)
            it.touchPointRadius = resources.dp(5F)
            it.selectedColor = ContextCompat.getColor(this, R.color.colorPrimary)
            it.focusColor = ContextCompat.getColor(this, R.color.colorAccent)
            it.pendingLayoutTime = 800L
            it
        }.onCantLayout {
            Toast.makeText(this, "出现了${it.size}个无法排版的View", Toast.LENGTH_SHORT).show()
            for (panel in it) {
                widgetHelper.removePanel(panel)
            }
        }

        widgetHelper.onSelectWidgetError {
            Toast.makeText(this, "选择系统小部件时出现异常", Toast.LENGTH_SHORT).show()
        }

        widgetHelper.addPanel(ClockPanelInfo())
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
