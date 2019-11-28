package liang.lollipop.electronicclock.offScreen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Size
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout

/**
 * @author lollipop
 * @date 2019-11-26 22:47
 * 离屏渲染引擎
 */
class OffScreenEngine(private val context: Context) {

    companion object {
        var FPS = 60
    }

    private var surface: Surface? = null

    private var viewSize = ViewSize(0, 0)

    private var padding = Inset(0, 0, 0, 0)

    fun setSize(width: Int, height: Int) {
        viewSize.reset(width, height)
    }

    fun draw(canvas: Canvas) {
    }

    fun onShow() {
    }

    fun onHide() {
    }

    private data class ViewSize(var width: Int, var height: Int) {
        fun reset(width: Int, height: Int) {
            this.width = width
            this.height = height
        }
    }

    private data class Inset(var left: Int, var top: Int,var right: Int, var bottom: Int) {
        fun reset(left: Int, top: Int, right: Int, bottom: Int) {
            this.left = left
            this.top = top
            this.right = right
            this.bottom = bottom
        }
    }

}