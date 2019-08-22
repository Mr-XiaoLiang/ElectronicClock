package liang.lollipop.electronicclock.activity.option

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.activity.BottomNavigationActivity

class BatteryPanelActivity : BottomNavigationActivity() {

    override val contentViewId: Int
        get() = R.layout.activity_battery_panel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_OK, Intent())
    }
}
