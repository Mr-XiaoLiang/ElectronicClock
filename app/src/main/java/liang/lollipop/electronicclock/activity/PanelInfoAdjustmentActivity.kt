package liang.lollipop.electronicclock.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import liang.lollipop.electronicclock.R

class PanelInfoAdjustmentActivity : BottomNavigationActivity() {

    override val contentViewId: Int
        get() = R.layout.activity_panel_info_adjustment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        showFAB(R.drawable.ic_done_black_24dp) {
            setResult(Activity.RESULT_OK, Intent())
            onBackPressed()
        }

    }

}
