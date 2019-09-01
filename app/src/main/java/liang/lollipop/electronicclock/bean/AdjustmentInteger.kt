package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整Boolean类型
 */
class AdjustmentInteger(run: AdjustmentInteger.() -> Unit):
    AdjustmentInfo<Int>(0) {
    init {
        run(this)
    }

    var min = 0
    var max = 100

}