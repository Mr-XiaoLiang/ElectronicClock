package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整padding类型
 */
class AdjustmentPadding(run: AdjustmentPadding.() -> Unit):
    AdjustmentInfo() {

    val paddings = FloatArray(4)

    init {
        run(this)
    }

    fun reset(p: FloatArray) {
        paddings[0] = p[0]
        paddings[1] = p[1]
        paddings[2] = p[2]
        paddings[3] = p[3]
    }

    override fun copy(info: AdjustmentInfo) {
        super.copy(info)
        if (info is AdjustmentPadding) {
            reset(info.paddings)
        }
    }

}