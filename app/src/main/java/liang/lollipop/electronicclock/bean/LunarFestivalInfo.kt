package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-09-29 23:50
 * 日历信息的节假日展示
 */
class LunarFestivalInfo(val name: String, val type: Int = TYPE_GREGORIAN) {

    companion object {
        const val TYPE_GREGORIAN = 0
        const val TYPE_LUNAR = 1
    }

}