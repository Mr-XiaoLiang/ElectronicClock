package liang.lollipop.electronicclock.wallpaper

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import liang.lollipop.electronicclock.drawable.WheelTimerDrawable
import liang.lollipop.electronicclock.offScreen.InvalidateCallback
import liang.lollipop.electronicclock.offScreen.Painter
import liang.lollipop.electronicclock.offScreen.PainterHelper

/**
 * @author lollipop
 * @date 2019-12-07 21:51
 * 滚轮时间的离屏渲染包装类
 */
class WheelTimerPainter(valueProvider: WheelTimerDrawable.ValueProvider) : Painter, Drawable.Callback {

    private val wheelTimerDrawable = WheelTimerDrawable(valueProvider).apply {
        callback = this@WheelTimerPainter
    }

    private val painterHelper = PainterHelper(wheelTimerDrawable)

    override fun setInvalidateCallback(callback: InvalidateCallback) {
        painterHelper.setInvalidateCallback(callback)
    }

    override fun onSizeChange(width: Int, height: Int) {
        painterHelper.onSizeChange(width, height)
    }

    override fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        painterHelper.onInsetChange(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        wheelTimerDrawable.draw(canvas)
    }

    override fun onShow() {
        wheelTimerDrawable.start()
    }

    override fun onHide() {
        wheelTimerDrawable.stop()
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {  }

    override fun invalidateDrawable(who: Drawable) {
        painterHelper.callInvalidate()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, whenTime: Long) {  }
}