package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.activity_main.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.PreferenceHelper
import liang.lollipop.electronicclock.utils.getPreferences
import liang.lollipop.electronicclock.utils.putPreferences
import liang.lollipop.electronicclock.widget.panel.LauncherPanel
import liang.lollipop.guidelinesview.Guidelines
import liang.lollipop.widget.utils.FloatingViewHelper


/**
 * @author Lollipop
 * 主页，提供导航跳转功能
 */
class MainActivity : BottomNavigationActivity() {

    companion object {
        private const val SHOW_START_BTN_GUIDELINES = "MAIN_ACTIVITY_SHOW_START_BTN_GUIDELINES"
    }

    override val contentViewId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.BLACK
        showFAB(R.drawable.ic_play_arrow_black_24dp) { fab ->
//            fab.setOnClickListener {
//                startActivity(Intent(this, WidgetActivity::class.java))
//            }
            LauncherPanel.AppPanel(fab)
        }
        PreferenceHelper.bindPreferenceGroup(preferenceGroup)

        showGuidelines()
    }

    private fun showGuidelines() {
        if (getPreferences(SHOW_START_BTN_GUIDELINES, true)) {
            Guidelines.target(fab).showIn(this).value(R.string.guidelines_start_btn).onClose {
                putPreferences(SHOW_START_BTN_GUIDELINES, false)
            }.show()
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
                return true
            }
            R.id.actionLandscapeBtn -> {
                EditActivity.startByLandscape(this)
                return true
            }
            R.id.actionLunar -> {
                startActivity(Intent(this, LunarActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
