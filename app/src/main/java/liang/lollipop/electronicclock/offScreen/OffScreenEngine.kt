package liang.lollipop.electronicclock.offScreen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
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

    private val rootView = ViewRoot(context)

    private var surface: Surface? = null

    private var viewSize = ViewSize(0, 0)

    init {
        rootView.onRequestLayout {
            requestLayout()
        }
    }

    fun addView(view: View, layoutParams: FrameLayout.LayoutParams) {
        rootView.addView(view, layoutParams)
    }

    fun addView(view: View) {
        rootView.addView(view)
    }

    fun addView(view: View, width: Int, height: Int) {
        rootView.addView(view, width, height)
    }

    fun addView(layoutId: Int) {
        rootView.addView(LayoutInflater.from(context).inflate(layoutId, rootView, true))
    }

    fun setSize(width: Int, height: Int) {
        viewSize.reset(width, height)
        requestLayout()
    }

    fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        rootView.setPadding(left, top, right, bottom)
    }

    fun draw(canvas: Canvas) {
        rootView.draw(canvas)
    }

    fun onShow() {
        rootView.callAttachedToWindow()
    }

    fun onHide() {
        rootView.callDetachedFromWindow()
    }

    private fun requestLayout() {
        rootView.measure(View.MeasureSpec.makeMeasureSpec(viewSize.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(viewSize.height, View.MeasureSpec.EXACTLY))
        rootView.layout(0, 0, viewSize.width, viewSize.height)
    }

    private class ViewRoot(context: Context): FrameLayout(context) {

        private var requestCallback: (() -> Unit)? = null

        override fun requestLayout() {
            super.requestLayout()
            requestCallback?.invoke()
        }

        fun callAttachedToWindow() {
            onAttachedToWindow()
        }

        fun callDetachedFromWindow() {
            onDetachedFromWindow()
        }

        fun onRequestLayout(callback: () -> Unit) {
            this.requestCallback = callback
        }
    }

    private data class ViewSize(var width: Int, var height: Int) {
        fun reset(width: Int, height: Int) {
            this.width = width
            this.height = height
        }
    }

}