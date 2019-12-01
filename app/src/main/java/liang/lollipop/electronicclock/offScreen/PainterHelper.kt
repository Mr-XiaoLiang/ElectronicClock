package liang.lollipop.electronicclock.offScreen

/**
 * @author lollipop
 * @date 2019-11-28 22:58
 * 绘制者的辅助类
 */
class PainterHelper {

    val viewSize = ViewSize(0, 0)
    val padding = Inset(0, 0, 0, 0)
    private var invalidateCallback: InvalidateCallback? = null

    fun onSizeChange(width: Int, height: Int) {
        viewSize.reset(width, height)
    }

    fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        padding.reset(left, top, right, bottom)
    }

    fun setInvalidateCallback(callback: InvalidateCallback) {
        invalidateCallback = callback
    }

    fun callInvalidate() {
        invalidateCallback?.requestInvalidate()
    }

    fun callDrawingEnd() {
        invalidateCallback?.drawingEnd()
    }

}