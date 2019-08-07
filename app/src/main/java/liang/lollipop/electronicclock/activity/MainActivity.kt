package liang.lollipop.electronicclock.activity

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import liang.lollipop.electronicclock.R
import liang.lollipop.widget.utils.Utils


/**
 * @author Lollipop
 */
class MainActivity : BaseActivity() {

    companion object {
        private val logger = Utils.loggerI("MainActivity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
