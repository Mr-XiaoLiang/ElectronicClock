package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整Boolean类型
 */
class AdjustmentColor(run: AdjustmentColor.() -> Unit):
    AdjustmentInfo() {

    val colors = ArrayList<Int>()

    var maxSize = -1
    var minSize = -1

    init {
        run(this)
    }

    fun reset(c: ArrayList<Int>) {
        colors.clear()
        colors.addAll(c)
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