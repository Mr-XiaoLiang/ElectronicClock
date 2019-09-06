package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整Boolean类型
 */
class AdjustmentInteger(run: AdjustmentInteger.() -> Unit):
    AdjustmentInfo() {

    var value = 0

    var min = 0
    var max = 100

    init {
        run(this)
    }

    override fun copy(info: AdjustmentInfo) {
        super.copy(info)
        if (info is AdjustmentInteger) {
            value = info.value
            max = info.max
            min = info.min
        }
    }

}