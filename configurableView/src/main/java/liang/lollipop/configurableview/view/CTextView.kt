package liang.lollipop.configurableview.view

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import liang.lollipop.configurableview.util.ConfigInfo

/**
 * @author lollipop
 * @date 2019-10-21 23:56
 * 可配置的TextView
 */
class CTextView(context: Context): TextView(context) {


    class TextConfigInfo(configInfo: ConfigInfo): ConfigInfo(configInfo) {
        companion object {
            const val TEXT = "TEXT"
            const val TEXT_COLOR = "TEXT_COLOR"
            const val TEXT_SIZE = "TEXT_SIZE"
        }

        var text: String
            set(value) {
                put(TEXT, value)
            }
            get() {
                return opt(TEXT, "")
            }

        var textColor: Int
            set(value) {
                put(TEXT_COLOR, value)
            }
            get() {
                return opt(TEXT_COLOR, Color.BLACK)
            }
        var textSize: Float
            set(value) {
                put(TEXT_SIZE, value)
            }
            get() {
                return opt(TEXT_SIZE, 16F)
            }

    }

}