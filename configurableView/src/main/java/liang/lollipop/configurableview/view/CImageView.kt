package liang.lollipop.configurableview.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import liang.lollipop.configurableview.util.ConfigInfo

/**
 * @author lollipop
 * @date 2019-10-25 23:48
 * 可以配置参数的ImageView
 */
class CImageView(context: Context): ImageView(context),
    ConfigurableViewInterface<CImageView.ImageConfigInfo> {

    override val viewConfigInfo = ImageConfigInfo()

    class ImageConfigInfo(configInfo: ConfigInfo? = null): ConfigInfo(configInfo) {

        companion object {
            const val IMAGE_PATH = "src"
            const val SRC_TYPE = "src"
            const val SCALE_TYPE = "scaleType"
        }

        var scaleType: Int
            set(value) {
                put(SCALE_TYPE, value)
            }
            get() {
                return opt(SCALE_TYPE, ScaleType.MATRIX.ordinal)
            }

        override fun onBindToView(view: View) {
            super.onBindToView(view)
            if (view is ImageView) {
                view.scaleType = ScaleType.values()[scaleType]
            }
        }

    }

}