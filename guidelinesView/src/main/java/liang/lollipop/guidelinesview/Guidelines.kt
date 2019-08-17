package liang.lollipop.guidelinesview

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import liang.lollipop.guidelinesview.util.GuidelinesBuilder
import liang.lollipop.guidelinesview.util.GuidelinesInfo

/**
 * @author lollipop
 * @date 2019-08-17 02:01
 */
object Guidelines {

    fun global(run: GuidelinesInfo.() -> Unit) {
        run(GuidelinesInfo)
    }

    fun target(view: View): GuidelinesBuilder {
        return GuidelinesBuilder(view)
    }

}