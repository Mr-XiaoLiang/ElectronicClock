package liang.lollipop.widget.utils

import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo

/**
 * 面板的提供类
 * @author Lollipop
 */
interface PanelProviders {

    /**
     * 根据描述信息创建一个面板对象
     * @param info 描述信息对象
     * 如果无法正确创建描述信息，或者找不到对应的面板，那么将会返回null，
     * 交由WidgetAdapter返回默认的面板对象
     */
    fun createPanelByInfo(info: PanelInfo): Panel<*>?

    /**
     * 根据类型名来创建一个描述信息对象
     * @author name 描述信息的字符串名称
     * @return 描述信息的对象实例
     * 如果找不到有效的对象，那么将返回null，
     * 则WidgetAdapter将会返回一个默认的空描述信息
     */
    fun createInfoByName(name: String): PanelInfo?


}