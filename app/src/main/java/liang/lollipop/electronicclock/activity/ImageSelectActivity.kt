package liang.lollipop.electronicclock.activity

import android.os.Bundle
import liang.lollipop.electronicclock.R

/**
 * 图片选择的页面
 * @author Lollipop
 */
class ImageSelectActivity : BottomNavigationActivity() {

    override val contentViewId: Int
        get() = R.layout.activity_select

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFAB(R.drawable.ic_done_black_24dp) {

        }
    }


}
