package liang.lollipop.electronicclock.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import liang.lollipop.electronicclock.R

/**
 * 黄历，农历的Activity
 * @author Lollipop
 */
class LunarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunar)
    }
}
