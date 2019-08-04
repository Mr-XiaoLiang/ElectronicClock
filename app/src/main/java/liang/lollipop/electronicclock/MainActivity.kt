package liang.lollipop.electronicclock

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.utils.dp
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author Lollipop
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private val logger = Utils.loggerI("MainActivity")
    }

    private lateinit var widgetHelper: WidgetHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        widgetHelper = WidgetHelper.with(widgetGroup).let {
            it.dragStrokeWidth = resources.dp(20F)
            it.selectedBorderWidth = resources.dp(2F)
            it.touchPointRadius = resources.dp(5F)
            it.selectedColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
            it.focusColor = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
            it.pendingLayoutTime = 800L
            it
        }.onCantLayout {
            Toast.makeText(this, "出现了${it.size}个无法排版的View", Toast.LENGTH_SHORT).show()
        }

        widgetHelper.addPanel(TestPanel(TestInfo(2, 1, Color.GREEN), "1"))
        widgetHelper.addPanel(TestPanel(TestInfo(1, 2, Color.RED), "2"))
        widgetHelper.addPanel(TestPanel(TestInfo(4, 2, Color.BLUE), "3"))
        widgetHelper.addPanel(TestPanel(TestInfo(2, 1, Color.GRAY), "4"))
        widgetHelper.addPanel(TestPanel(TestInfo(2, 1, Color.CYAN), "5"))
        widgetHelper.addPanel(TestPanel(TestInfo(3, 1, Color.LTGRAY), "6"))
        widgetHelper.addPanel(ClockPanelInfo())
        widgetHelper.addPanel(PanelInfo())
    }

    override fun onStart() {
        super.onStart()
        widgetHelper.onStart()
    }

    override fun onStop() {
        super.onStop()
        widgetHelper.onStop()
    }

    private class TestPanel(info: TestInfo, val value: String): Panel<TestInfo>(info) {
        override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
            val view = TextView(layoutInflater.context)
            view.setBackgroundColor(panelInfo.color)
            view.text = value
            view.gravity = Gravity.CENTER
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22F)
            view.setTextColor(Color.WHITE)
            return view
        }
    }

    private class TestInfo(sX: Int, sY: Int, c: Int): PanelInfo() {
        init {
            spanX = sX
            spanY = sY
            color = c
        }
    }

}
