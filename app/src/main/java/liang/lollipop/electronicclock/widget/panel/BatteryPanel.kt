package liang.lollipop.electronicclock.widget.panel

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.electronicclock.widget.info.BatteryInfo
import liang.lollipop.widget.widget.Panel

/**
 * @author lollipop
 * @date 2019-08-19 22:15
 * 电池信息的展示面板
 */
class BatteryPanel(info: BatteryInfo): Panel<BatteryInfo>(info) {
    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    private class BatteryDrawable(private var info: BatteryInfo): Drawable() {

        fun onInfoChange(newInfo: BatteryInfo) {
            info = newInfo
            relayout()
        }

        private fun relayout() {

        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            relayout()
        }

        override fun draw(canvas: Canvas) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setAlpha(alpha: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getOpacity(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

}