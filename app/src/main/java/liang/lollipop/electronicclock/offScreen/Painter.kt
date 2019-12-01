package liang.lollipop.electronicclock.offScreen

import android.graphics.Canvas

/**
 * @author lollipop
 * @date 2019-11-28 22:55
 * 绘制者的接口
 */
interface Painter {
    fun setInvalidateCallback(callback: InvalidateCallback)
    fun onSizeChange(width: Int, height: Int)
    fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int)
    fun draw(canvas: Canvas)
    fun onShow()
    fun onHide()
}