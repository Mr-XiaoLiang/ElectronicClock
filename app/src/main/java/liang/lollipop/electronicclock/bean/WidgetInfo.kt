package liang.lollipop.electronicclock.bean

import liang.lollipop.electronicclock.edit.EditAction

/**
 * @author lollipop
 * @date 2019-11-13 21:15
 * 小部件的信息
 */
class WidgetInfo(icon: Int, name: Int, val infoName: String):
    ActionInfo(EditAction.DONE, icon, name)