package liang.lollipop.electronicclock.list

/**
 * @author lollipop
 * @date 2019-08-15 22:13
 * 数字类型的偏好设置holder
 */
class PreferenceNumber : PreferenceInfo<Int>() {

    override var value: Int = 0

    var max = Int.MAX_VALUE
    var min = 0

}