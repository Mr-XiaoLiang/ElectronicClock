package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.ActionMenuView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.activity_main.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.PreferenceHelper
import liang.lollipop.guidelinesview.Guidelines
import liang.lollipop.widget.utils.Utils


/**
 * @author Lollipop
 * 主页，提供导航跳转功能
 */
class MainActivity : BottomNavigationActivity() {

    companion object {
        private val logger = Utils.loggerI("MainActivity")
    }

    override val contentViewId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFAB(R.drawable.ic_play_arrow_black_24dp) {
            it.setOnClickListener {
//                startActivity(Intent(this, WidgetActivity::class.java))
                Guidelines.target(fab).showIn(this).value(R.string.guidelines_start_btn)
                    .next(appBarLayout).value("这是一个APP bar layout").show()
            }
        }
        PreferenceHelper.bindPreferenceGroup(preferenceGroup)

        Guidelines.global {
            fontColor = Color.WHITE
            fontSize = 18F
            panelColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
            backgroundColor = changeAlpha(Color.BLACK, 200)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.actionProtraitBtn -> {
                EditActivity.startByPortrait(this)
            }
            R.id.actionLandscapeBtn -> {
                EditActivity.startByLandscape(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
