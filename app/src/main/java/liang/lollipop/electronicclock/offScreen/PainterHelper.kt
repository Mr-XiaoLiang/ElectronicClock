package liang.lollipop.electronicclock.offScreen

import android.graphics.drawable.Drawable

/**
 * @author lollipop
 * @date 2019-11-28 22:58
 * 绘制者的辅助类
 */
class PainterHelper(private val drawable: Drawable? = null) {

    val viewSize = ViewSize(0, 0)
    val padding = Inset(0, 0, 0, 0)
    private var invalidateCallback: InvalidateCallback? = null

    var isShown = false
        private set

    fun onSizeChange(width: Int, height: Int) {
        viewSize.reset(width, height)
        setBounds()
    }

    fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        padding.reset(left, top, right, bottom)
        setBounds()
    }

    private fun setBounds() {
        drawable?.setBounds(padding.left, padding.top,
            viewSize.width - padding.right, viewSize.height - padding.bottom)
    }

    fun setInvalidateCallback(callback: InvalidateCallback) {
        invalidateCallback = callback
    }

    fun callInvalidate() {
        invalidateCallback?.requestInvalidate()
        drawable?.invalidateSelf()
    }

    fun callDrawingEnd() {
        invalidateCallback?.drawingEnd()
    }

    fun onShow() {
        isShown = true
    }

    fun onHide() {
        isShown = false
    }

}