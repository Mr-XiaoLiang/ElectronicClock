package liang.lollipop.configurableview.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import liang.lollipop.configurableview.util.ConfigInfo
import java.io.File

/**
 * @author lollipop
 * @date 2019-10-21 23:56
 * 可配置的TextView
 */
class CTextView(context: Context): TextView(context),
    ConfigurableViewInterface<CTextView.TextConfigInfo> {

    override val viewConfigInfo = TextConfigInfo()

    class TextConfigInfo(configInfo: ConfigInfo? = null): ConfigInfo(configInfo) {

        companion object {
            const val TEXT = "text"
            const val TEXT_COLOR = "textColor"
            const val TEXT_SIZE = "textSize"
            const val FONT_PATH = "fontPath"
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

        var fontPath: String
            set(value) {
                put(FONT_PATH, value)
            }
            get() {
                return opt(FONT_PATH, "")
            }

        override fun onBindToView(view: View) {
            super.onBindToView(view)
            if (view is TextView) {
                view.text = text
                view.setTextColor(textColor)
                view.textSize = textSize
                view.gravity = gravity
                if (fontPath.isNotEmpty()) {
                    val fontFile = File(fontPath)
                    if (fontFile.exists() && fontFile.canRead()) {
                        view.typeface = Typeface.createFromFile(fontFile)
                    }
                }
            }
        }

    }

}