package liang.lollipop.electronicclock.edit

enum class EditAction(val id: Int) {
    /** 删除 **/
    DELETE(-1),
    /** 完成 **/
    DONE(0),
    /** 返回 **/
    BACK(1),
    /** 系统小部件 **/
    WIDGET(2),
    /** 预览 **/
    PREVIEW(3),
    /** 颜色反转 **/
    INVERTED(4),
    /** 自动亮度 **/
    AUTO_LIGHT(5),
    /** 重置 **/
    RESET(6),
    /** 调整 **/
    ADJUSTMENT(7),
}