package liang.lollipop.electronicclock.offScreen

/**
 * @author lollipop
 * @date 2019-12-01 13:04
 * 重绘的回调函数
 */
interface InvalidateCallback {

    fun requestInvalidate()

    fun drawingEnd()

}