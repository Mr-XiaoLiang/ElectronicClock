package liang.lollipop.configurableview.view

import android.content.Context
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
        }

        var text: String
            set(value) {
                put(TEXT, value)
            }
            get() {
                return opt(TEXT, "")
            }

    }

}