package liang.lollipop.electronicclock.offScreen

/**
 * @author lollipop
 * @date 2019-12-01 12:51
 * View的尺寸
 */
data class ViewSize(var width: Int, var height: Int) {
    val isEmpty: Boolean
        get() {
            return width < 1 || height < 1
        }

    fun reset(width: Int, height: Int): Boolean {
        val isChanged = isChange(width, height)
        this.width = width
        this.height = height
        return isChanged
    }
    fun isChange(width: Int, height: Int): Boolean {
        return width != this.width || height != this.height
    }
}