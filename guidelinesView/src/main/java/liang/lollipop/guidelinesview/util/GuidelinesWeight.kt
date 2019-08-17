package liang.lollipop.guidelinesview.util

import java.lang.RuntimeException

/**
 * @author lollipop
 * @date 2019-08-17 02:20
 * 引导组件的计算权重信息
 * 包含一个简单的参数锁，防止在任务开始时由于参数修改造成数据错误
 */
class GuidelinesWeight(associationValue: Association, sideValue: Side, weightValue: Float, locked: Boolean = false) {

    var association: Association = associationValue
        set(value) {
            checkLocked()
            field = value
        }
    var side: Side = sideValue
        set(value) {
            checkLocked()
            field = value
        }
    var weight: Float = weightValue
        set(value) {
            checkLocked()
            field = value
        }

    var isLocked = locked
        set(value) {
            if (field && !value) {
                throw RuntimeException("Already locked, can't be unlocked again")
            }
            field = value
        }

    private fun checkLocked() {
        if (isLocked) {
            throw RuntimeException("Already locked, can't be changed again")
        }
    }

}