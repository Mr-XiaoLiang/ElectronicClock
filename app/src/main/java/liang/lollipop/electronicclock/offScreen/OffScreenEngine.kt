package liang.lollipop.electronicclock.offScreen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout

/**
 * @author lollipop
 * @date 2019-11-26 22:47
 * 离屏渲染引擎
 */
class OffScreenEngine(private val context: Context) {

    private val rootView = ViewRoot(context)

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
        rootView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        rootView.layout(0, 0, width, height)
    }

    fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        rootView.setPadding(left, top, right, bottom)
    }

    fun draw(canvas: Canvas) {
        rootView.draw(canvas)
        rootView.post {  }
    }

    private class ViewRoot(context: Context): FrameLayout(context) {
        override fun requestLayout() {
            super.requestLayout()
        }
    }

}