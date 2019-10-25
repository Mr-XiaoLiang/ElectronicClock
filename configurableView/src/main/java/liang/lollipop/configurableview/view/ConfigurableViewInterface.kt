package liang.lollipop.configurableview.view

import android.view.View
import liang.lollipop.configurableview.util.ConfigInfo

/**
 * @author lollipop
 * @date 2019-10-25 00:23
 * 可配置View的接口
 */
interface ConfigurableViewInterface<T: ConfigInfo> {

    val viewConfigInfo: T

    fun bindData(info: ConfigInfo) {
        viewConfigInfo.parse(info)
        if (this is View) {
            viewConfigInfo.bindToView(this)
        }
    }

    fun serializat(): ConfigInfo {
        return viewConfigInfo
    }

}