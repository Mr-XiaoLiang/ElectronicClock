package liang.lollipop.electronicclock.activity

import kotlinx.android.synthetic.main.activity_lunar.*
import liang.lollipop.electronicclock.R

/**
 * 黄历，农历的Activity
 * @author Lollipop
 */
class LunarActivity : BottomNavigationActivity() {

    override val contentViewId: Int
        get() = R.layout.activity_lunar

    override fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        super.onWindowInsetsChange(left, top, right, bottom)
        topBounds.setGuidelineBegin(top)
    }

    override fun filterRootGroupInset(left: Int, top: Int, right: Int, bottom: Int): IntArray {
        return intArrayOf(left, 0, right, bottom)
    }

}
