package liang.lollipop.electronicclock

import android.content.Intent
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
import liang.lollipop.electronicclock.activity.BaseActivity
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.utils.dp
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo
import java.util.*


/**
 * @author Lollipop
 */
class MainActivity : BaseActivity() {

    companion object {
        private val logger = Utils.loggerI("MainActivity")
    }

    private lateinit var widgetHelper: WidgetHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingBtn.setOnClickListener {
//            widgetHelper.selectAppWidget()
            startActivity(Intent(this, WidgetActivity::class.java))
        }
    }

}
