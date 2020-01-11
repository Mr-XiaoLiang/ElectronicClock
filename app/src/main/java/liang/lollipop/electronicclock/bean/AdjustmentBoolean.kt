package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整Boolean类型
 */
class AdjustmentBoolean: AdjustmentInfo() {

    var value = false

    var summaryOfTrue = ""
    var summaryOfFalse = ""

    override fun copy(info: AdjustmentInfo) {
        super.copy(info)
        if (info is AdjustmentBoolean) {
            value = info.value
            summaryOfFalse = info.summaryOfFalse
            summaryOfTrue = info.summaryOfTrue
        }
    }

}