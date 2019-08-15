package liang.lollipop.electronicclock.list

/**
 * @author lollipop
 * @date 2019-08-15 22:14
 * 偏好设置的信息类
 */
abstract class PreferenceInfo<T> {

    var key = ""
    abstract var value: T
    var title = ""
    var summer = ""

}