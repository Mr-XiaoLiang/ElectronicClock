package liang.lollipop.guidelinesview.util

import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

/**
 * @author lollipop
 * @date 2019-08-17 02:12
 * 引导页面的相关描述信息
 */
object GuidelinesInfo {

    /**
     * 面板的半径
     * 面板是圆形，它的半径默认情况下是容器的短边的1倍
     */
    var panelRadius = GuidelinesWeight(Association.Group, Side.Shortest, 1F, true)

    /**
     * 默认的内补白
     * 用于间隔面板和目标View之间的距离
     */
    var paddingSize = GuidelinesWeight(Association.Self, Side.Shortest, 0.5F, true)

    /**
     * 面板的颜色
     */
    var panelColor = Color.GREEN

    /**
     * 背景色
     */
    var backgroundColor = changeAlpha(Color.BLACK, 128)

    /**
     * 文字的颜色
     */
    var fontColor = Color.WHITE

    /**
     * 文字大小
     */
    var fontSize = -1F

    /**
     * 动画持续时间
     */
    var animationDuration = 300L

    fun changeAlpha(color: Int, alpha: Int): Int {
        var a = alpha
        if (a < 0) {
            a = 0
        }
        if (a > 255) {
            a = 255
        }
        return color and 0xFFFFFF or a.shl(24)
    }

    fun getSizeBySide(width: Float, height: Float, side: Side): Float {
        return when (side) {
            Side.Height -> height
            Side.Width -> width
            Side.Longest -> max(width, height)
            Side.Shortest -> min(width, height)
        }
    }



}