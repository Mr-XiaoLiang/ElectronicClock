package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-08-15 22:13
 * 是非类型的偏好设置holder
 */
class PreferenceChoice : PreferenceInfo<Int>() {

    override var value: Int =
        VALUE_NEUTRAL

    var positiveIcon = 0
    var negativeIcon = 0
    var neutralIcon = 0

    var positiveName = ""
    var negativeName = ""
    var neutralName = ""

    var dialogMessage: String? = null

    companion object {
        /**
         * 积极的
         */
        const val VALUE_POSITIVE = 1
        /**
         * 消极的
         */
        const val VALUE_NEGATIVE = -1
        /**
         * 中性的
         */
        const val VALUE_NEUTRAL = 0
    }

}