package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:47
 */
open class AdjustmentInfo {

    var title = ""
    var summary = ""
    var key = ""
    var enable = true

    open fun copy(info: AdjustmentInfo) {
        title = info.title
        summary = info.summary
        key = info.key
        enable = info.enable
    }

}