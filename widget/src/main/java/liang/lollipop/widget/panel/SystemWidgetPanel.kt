package liang.lollipop.widget.panel

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.view.*
import android.widget.FrameLayout
import liang.lollipop.widget.info.SystemWidgetPanelInfo
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.WidgetGroup
import kotlin.math.abs


/**
 * @author lollipop
 * @date 2019-08-05 13:24
 * 系统小部件的面板
 */
class SystemWidgetPanel(info: SystemWidgetPanelInfo,
                        private val widgetView: AppWidgetHostView): Panel<SystemWidgetPanelInfo>(info) {

    companion object {
        private fun pointInView(v: View, localX: Float, localY: Float, slop: Int): Boolean {
            return localX >= -slop && localY >= -slop && localX < v.width + slop &&
                    localY < v.height + slop
        }
    }

    override fun updatePanelInfo(group: WidgetGroup) {
        super.updatePanelInfo(group)
        if (panelInfo.updateSpanByGroup) {
            return
        }
        val gridSize = group.gridSize
        if (gridSize.width <= 0 || gridSize.height <= 0) {
            return
        }
        // 如果是空的，那么直接将尺寸设置为0
        if (panelInfo.isEmpty) {
            panelInfo.sizeChange(0, 0)
            return
        }
        panelInfo.updateSpanByGroup = true
        val providerInfo = panelInfo.appWidgetProviderInfo
        val minWidth = providerInfo.minWidth
        val minHeight = providerInfo.minHeight
        var spanX = minWidth / gridSize.width
        if (minWidth % gridSize.width != 0) {
            spanX++
        }
        if (spanX < 1) {
            spanX = 1
        }
        var spanY = minHeight / gridSize.height
        if (minHeight % gridSize.height != 0) {
            spanY++
        }
        if (spanY < 1) {
            spanY = 1
        }
        panelInfo.sizeChange(spanX, spanY)
    }

    override fun onSizeChange(width: Int, height: Int) {
        super.onSizeChange(width, height)
        widgetView.updateAppWidgetSize(null, width, height, width, height)
        widgetView.requestLayout()
    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        view?.alpha = light
    }

    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return LongPressGroup(layoutInflater.context).apply {
            addView(widgetView)
        }
    }

    private class LongPressGroup(context: Context): FrameLayout(context) {

        private val logger = Utils.loggerI("LongPressGroup")

        private val longPressHelper = CheckLongPressHelper(this)

        private val stylusEventHelper = StylusEventHelper({
            logger("onPressed isLongClickable: $isLongClickable")
            isLongClickable && performLongClick()
        }, { false }, this)

        private val slop = ViewConfiguration.get(getContext()).scaledTouchSlop

        private val touchDown = PointF()

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            logger("onLayout")
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view.visibility != View.GONE) {
                    view.layout(0, 0, right - left, bottom - top)
                }
            }
        }

        override fun performLongClick(): Boolean {
            val result = super.performLongClick()
            logger("performLongClick： $result")
            return result
        }

        override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
            if (ev?.action == MotionEvent.ACTION_DOWN) {
                longPressHelper.cancelLongPress()
            }
            if (longPressHelper.hasPerformedLongPress) {
                longPressHelper.cancelLongPress()
                logger("hasPerformedLongPress -> cancelLongPress")
                return true
            }
            if (stylusEventHelper.onMotionEvent(ev)) {
                longPressHelper.cancelLongPress()
                logger("stylusEventHelper.onMotionEvent -> cancelLongPress")
                return true
            }
            when (ev?.actionMasked) {
                MotionEvent.ACTION_DOWN -> if (!stylusEventHelper.isButtonPressed) {
                    logger("postCheckForLongPress")
                    longPressHelper.postCheckForLongPress()
                    touchDown.set(ev.x, ev.y)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    longPressHelper.cancelLongPress()
                    logger("ACTION_CANCEL -> cancelLongPress")
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = ev.x
                    val y = ev.y
                    if (!pointInView(this, x, y, slop)
                        || abs(x - touchDown.x) > slop
                        || abs(y - touchDown.y) > slop) {
                        longPressHelper.cancelLongPress()
                        logger("ACTION_MOVE -> cancelLongPress")
                    }
                }
            }
            return false
        }

        override fun onTouchEvent(ev: MotionEvent?): Boolean {
            when (ev?.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    longPressHelper.cancelLongPress()
                }
                MotionEvent.ACTION_MOVE -> if (pointInView(this, ev.x, ev.y, slop)) {
                    longPressHelper.cancelLongPress()
                }
            }
            return false
        }

        override fun cancelLongPress() {
            super.cancelLongPress()
            logger("cancelLongPress")
            longPressHelper.cancelLongPress()
        }

    }

    private class CheckLongPressHelper(private val targetView: View) {
        companion object {
            private const val DEFAULT_LONG_PRESS_TIMEOUT = 300L
        }

        private val logger = Utils.loggerI("CheckLongPressHelper")

        var hasPerformedLongPress = false
            private set

        var longPressTimeout = DEFAULT_LONG_PRESS_TIMEOUT

        private var longPressPending: CheckForLongPress? = null

        private inner class CheckForLongPress: Runnable {
            override fun run() {
                if (targetView.parent != null && targetView.hasWindowFocus()
                    && !hasPerformedLongPress) {
                    logger("CheckForLongPress.run")
                    val handled = targetView.performLongClick()
                    if (handled) {
                        targetView.isPressed = false
                        hasPerformedLongPress = true
                    }
                }
            }
        }

        fun postCheckForLongPress() {
            logger("postCheckForLongPress")
            hasPerformedLongPress = false
            if (longPressPending == null) {
                longPressPending = CheckForLongPress()
            }
            targetView.postDelayed(longPressPending, longPressTimeout)
        }

        fun cancelLongPress() {
            logger("cancelLongPress")
            hasPerformedLongPress = false
            if (longPressPending != null) {
                targetView.removeCallbacks(longPressPending)
                longPressPending = null
            }
        }

    }

    class StylusEventHelper(private val onPressed: ((view: View) -> Boolean)?,
                            private val onReleased: ((view: View) -> Boolean)?,
                            private val targetView: View) {

        var isButtonPressed: Boolean = false
        private val slop = ViewConfiguration.get(targetView.context).scaledTouchSlop

        fun onMotionEvent(event: MotionEvent?): Boolean {
            event?:return false
            val stylusButtonPressed = isStylusButtonPressed(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isButtonPressed = stylusButtonPressed
                    if (isButtonPressed) {
                        return onPressed?.invoke(targetView)?:false
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!pointInView(targetView, event.x, event.y, slop)) {
                        return false
                    }
                    if (!isButtonPressed && stylusButtonPressed) {
                        isButtonPressed = true
                        return onPressed?.invoke(targetView)?:false
                    } else if (isButtonPressed && !stylusButtonPressed) {
                        isButtonPressed = false
                        return onReleased?.invoke(targetView)?:false
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (isButtonPressed) {
                    isButtonPressed = false
                    return onReleased?.invoke(targetView)?:false
                }
            }
            return false
        }

        private fun isStylusButtonPressed(event: MotionEvent): Boolean {
            return event.getToolType(0) === MotionEvent.TOOL_TYPE_STYLUS
                    && event.buttonState and MotionEvent.BUTTON_SECONDARY === MotionEvent.BUTTON_SECONDARY
        }
    }



}