package liang.lollipop.electronicclock.offScreen

/**
 * @author lollipop
 * @date 2019-12-01 12:52
 * 缩进
 */
data class Inset(var left: Int, var top: Int,var right: Int, var bottom: Int) {
    fun reset(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        val isChanged = isChange(left, top, right, bottom)
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        return isChanged
    }
    fun isChange(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        return left != this.left || top != this.top || right != this.right || bottom != this.bottom
    }
}