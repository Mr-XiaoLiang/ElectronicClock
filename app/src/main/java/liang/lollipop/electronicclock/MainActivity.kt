package liang.lollipop.electronicclock

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
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
import liang.lollipop.electronicclock.utils.Utils
import liang.lollipop.electronicclock.utils.dp
import liang.lollipop.electronicclock.widget.Panel
import liang.lollipop.electronicclock.widget.PanelInfo
import liang.lollipop.electronicclock.widget.WidgetGroup

/**
 * @author Lollipop
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private val logger = Utils.loggerI("MainActivity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        widgetGroup.dragStrokeWidth = resources.dp(20F)

        var cantLayoutSize = 0
        widgetGroup.onCantLayout {
            cantLayoutSize++
            Toast.makeText(this, "出现了${cantLayoutSize}个无法排版的View", Toast.LENGTH_SHORT).show()
        }

        widgetGroup.addPanel(TestPanel(TestInfo(2, 1, Color.GREEN), "1"))
        widgetGroup.addPanel(TestPanel(TestInfo(1, 2, Color.RED), "2"))
        widgetGroup.addPanel(TestPanel(TestInfo(4, 2, Color.BLUE), "3"))
        widgetGroup.addPanel(TestPanel(TestInfo(2, 1, Color.GRAY), "4"))
        widgetGroup.addPanel(TestPanel(TestInfo(2, 1, Color.CYAN), "5"))
        widgetGroup.addPanel(TestPanel(TestInfo(3, 1, Color.LTGRAY), "6"))
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
        }
        var color: Int = c
    }

}
