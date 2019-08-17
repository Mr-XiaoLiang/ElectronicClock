package liang.lollipop.guidelinesview.util

/**
 * @author lollipop
 * @date 2019-08-17 02:28
 * 用于描述对象某一侧的枚举
 */
enum class Side {
    /**
     * 矩形的横向尺寸
     */
    Width,
    /**
     * 矩形的纵向尺寸
     */
    Height,
    /**
     * 矩形的纵横尺寸中较长的那一个
     */
    Longest,
    /**
     * 矩形的纵横尺寸中较短的那一个
     */
    Shortest

}