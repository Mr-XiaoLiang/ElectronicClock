package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整Boolean类型
 */
class AdjustmentBoolean(run: AdjustmentBoolean.() -> Unit):
    AdjustmentInfo<Boolean>(false) {
    init {
        run(this)
    }
}