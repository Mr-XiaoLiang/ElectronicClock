package liang.lollipop.configurableview.view

import liang.lollipop.configurableview.util.ConfigInfo

/**
 * @author lollipop
 * @date 2019-10-25 00:23
 * 可配置View的接口
 */
interface ConfigurableViewInterface {

    fun bindData(info: ConfigInfo)

    fun serializat(): ConfigInfo

}