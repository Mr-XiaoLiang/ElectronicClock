package liang.lollipop.electronicclock.view

/**
 * @author lollipop
 * @date 2019-10-06 18:02
 * view的按比例内补白接口，并且附带配套的实现方法
 */
interface WeightPaddingView {

    val paddingLeftW: Float
    val paddingTopW: Float
    val paddingRightW: Float
    val paddingBottomW: Float

    fun setWeightPadding(left: Float, top: Float, right: Float, bottom: Float)

}

class WeightPaddingViewHelper {
    private var left = 0F
    private var top = 0F
    private var right = 0F
    private var bottom = 0F

    fun setWeightPadding(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    fun paddingLeftW(width: Int): Float {
        return width * left
    }
    fun paddingTopW(height: Int): Float {
        return height * top
    }
    fun paddingRightW(width: Int): Float {
        return width * right
    }
    fun paddingBottomW(height: Int): Float {
        return height * bottom
    }
}