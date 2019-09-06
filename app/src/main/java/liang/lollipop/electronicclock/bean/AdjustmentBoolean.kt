package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整Boolean类型
 */
class AdjustmentBoolean(run: AdjustmentBoolean.() -> Unit):
    AdjustmentInfo() {

    var value = false

    var summaryOfTrue = ""
    var summaryOfFalse = ""

    init {
        run(this)
    }

    override fun copy(info: AdjustmentInfo) {
        super.copy(info)
        if (info is AdjustmentBoolean) {
            value = info.value
            summaryOfFalse = info.summaryOfFalse
            summaryOfTrue = info.summaryOfTrue
        }
    }

}