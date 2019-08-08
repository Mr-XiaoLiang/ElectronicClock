package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import liang.lollipop.electronicclock.R
import liang.lollipop.widget.utils.Utils


/**
 * @author Lollipop
 * 主页，提供导航跳转功能
 */
class MainActivity : BaseActivity() {

    companion object {
        private val logger = Utils.loggerI("MainActivity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindToolBar(toolbar as Toolbar)
        initInsetListener(rootGroup)
        protraitEditBtn.setOnClickListener {
            EditActivity.startByPortrait(this)
        }

        landscapeEditBtn.setOnClickListener {
            EditActivity.startByLandscape(this)
        }

        startBtn.setOnClickListener {
            startActivity(Intent(this, WidgetActivity::class.java))
        }
    }

}
