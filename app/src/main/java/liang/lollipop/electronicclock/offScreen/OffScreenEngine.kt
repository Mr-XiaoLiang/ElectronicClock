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
    }

    private class ViewRoot(context: Context): FrameLayout(context) {
        override fun requestLayout() {
            super.requestLayout()
        }
    }

    private class ViewRootManager: ViewParent {
        override fun getChildVisibleRect(child: View?, r: Rect?, offset: Point?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startActionModeForChild(
            originalView: View?,
            callback: ActionMode.Callback?
        ): ActionMode {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startActionModeForChild(
            originalView: View?,
            callback: ActionMode.Callback?,
            type: Int
        ): ActionMode {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isTextDirectionResolved(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getTextDirection(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onNestedScroll(
            target: View?,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun invalidateChild(child: View?, r: Rect?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onNestedPrePerformAccessibilityAction(
            target: View?,
            action: Int,
            arguments: Bundle?
        ): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getLayoutDirection(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canResolveTextDirection(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canResolveLayoutDirection(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun keyboardNavigationClusterSearch(currentCluster: View?, direction: Int): View {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onNestedScrollAccepted(child: View?, target: View?, nestedScrollAxes: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onNestedFling(
            target: View?,
            velocityX: Float,
            velocityY: Float,
            consumed: Boolean
        ): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getTextAlignment(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onNestedPreFling(target: View?, velocityX: Float, velocityY: Float): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun focusableViewAvailable(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getParentForAccessibility(): ViewParent {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canResolveTextAlignment(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun notifySubtreeAccessibilityStateChanged(
            child: View?,
            source: View,
            changeType: Int
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getParent(): ViewParent {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun requestTransparentRegion(child: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun createContextMenu(menu: ContextMenu?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun requestLayout() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun childHasTransientStateChanged(child: View?, hasTransientState: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun invalidateChildInParent(location: IntArray?, r: Rect?): ViewParent {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun requestFitSystemWindows() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isLayoutDirectionResolved(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onStopNestedScroll(target: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun bringChildToFront(child: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun focusSearch(v: View?, direction: Int): View {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun requestChildRectangleOnScreen(
            child: View?,
            rectangle: Rect?,
            immediate: Boolean
        ): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun childDrawableStateChanged(child: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onStartNestedScroll(
            child: View?,
            target: View?,
            nestedScrollAxes: Int
        ): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isLayoutRequested(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun recomputeViewAttributes(child: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun showContextMenuForChild(originalView: View?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun showContextMenuForChild(originalView: View?, x: Float, y: Float): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun requestSendAccessibilityEvent(
            child: View?,
            event: AccessibilityEvent?
        ): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun requestChildFocus(child: View?, focused: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isTextAlignmentResolved(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun clearChildFocus(child: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}