package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整Boolean类型
 */
class AdjustmentColor: AdjustmentInfo() {

    val colors = ArrayList<Int>()

    var maxSize = -1
    var minSize = -1

    fun reset(c: ArrayList<Int>) {
        colors.clear()
        colors.addAll(c)
    }

    fun reset(c: Int) {
        colors.clear()
        colors.add(c)
        maxSize = 1
        minSize = 1
    }

    override fun copy(info: AdjustmentInfo) {
        super.copy(info)
        if (info is AdjustmentColor) {
            reset(info.colors)
            maxSize = info.maxSize
            minSize = info.minSize
        }
    }

}