package liang.lollipop.guidelinesview.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import liang.lollipop.guidelinesview.util.Association
import liang.lollipop.guidelinesview.util.GuidelinesBuilder
import liang.lollipop.guidelinesview.util.GuidelinesInfo
import liang.lollipop.guidelinesview.util.Location
import kotlin.math.max
import kotlin.math.min

/**
 * @author lollipop
 * @date 2019-08-17 14:11
 */
class GuidelinesView(context: Context, attr: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    FrameLayout(context, attr, defStyleAttr, defStyleRes), ValueAnimator.AnimatorUpdateListener{

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int): this(context, attr, defStyleAttr, 0)
    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

    var builder: GuidelinesBuilder? = null
        set(value) {
            field = value
            initGuidelines()
        }

    private val messageView = TextView(context).apply {
        gravity = Gravity.CENTER
    }

    private val hollowCircleDrawable = HollowCircleDrawable()

    private val targetRect = Rect()

    private val targetLocal = IntArray(2)

    private val myLocal = IntArray(2)

    private val lastMessageBounds = Rect()

    private var animationProcess = 0F

    private val animator: ValueAnimator by lazy {
        ValueAnimator().apply {
            addUpdateListener(this@GuidelinesView)
        }
    }

    init {
        addView(messageView)
        background = hollowCircleDrawable
        elevation = 100F
    }

    private fun initGuidelines() {
        val info = builder?:return
        messageView.text = info.message
        if (info.fontSize > 0) {
            messageView.textSize = info.fontSize
        }
        messageView.setTextColor(info.fontColor)

        hollowCircleDrawable.backgroundColor = info.backgroundColor
        hollowCircleDrawable.foregroundColor = info.panelColor

        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        builder?:return
        if (!lastMessageBounds.isEmpty) {
            messageView.measure(MeasureSpec.makeMeasureSpec(lastMessageBounds.width(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(lastMessageBounds.height(), MeasureSpec.EXACTLY))
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val info = builder?:return

        targetRect.set(0, 0, info.target.width, info.target.height)
        info.target.getLocationOnScreen(targetLocal)
        getLocationOnScreen(myLocal)
        targetRect.offset(targetLocal[0] - myLocal[0], targetLocal[1] - myLocal[1])

        layoutPanel(info)

        layoutMessageView(info, 0, 0, right - left, bottom - top)
    }

    private fun layoutPanel(info: GuidelinesBuilder) {
        val paddingSide = info.paddingSize.side
        val paddingWeight = info.paddingSize.weight
        val paddingSize: Float
        when (info.paddingSize.association) {
            Association.Value -> paddingSize = paddingWeight
            Association.Self -> {
                val target = info.target
                paddingSize = paddingWeight * GuidelinesInfo.getSizeBySide(
                    target.width.toFloat(), target.height.toFloat(), paddingSide)
            }
            Association.Parent -> {
                val targetParent = info.targetParent
                paddingSize = paddingWeight * GuidelinesInfo.getSizeBySide(
                    targetParent.width.toFloat(), targetParent.height.toFloat(), paddingSide)
            }
            Association.Group -> {
                val group = parent as ViewGroup
                paddingSize = paddingWeight * GuidelinesInfo.getSizeBySide(
                    group.width.toFloat(), group.height.toFloat(), paddingSide)
            }
            Association.Screen -> {
                val w = resources.displayMetrics.widthPixels
                val h = resources.displayMetrics.heightPixels
                paddingSize = paddingWeight * GuidelinesInfo.getSizeBySide(
                    w.toFloat(), h.toFloat(), paddingSide)
            }
        }
        hollowCircleDrawable.hollow.set(targetRect.left - paddingSize, targetRect.top - paddingSize,
            targetRect.right + paddingSize, targetRect.bottom + paddingSize)

        val panelSide = info.panelRadius.side
        val radiusWeight = info.panelRadius.weight
        val radius: Float
        when (info.panelRadius.association) {
            Association.Value -> radius = radiusWeight
            Association.Self -> {
                val target = info.target
                radius = radiusWeight * GuidelinesInfo.getSizeBySide(
                    target.width.toFloat(), target.height.toFloat(), panelSide)
            }
            Association.Parent -> {
                val targetParent = info.targetParent
                radius = radiusWeight * GuidelinesInfo.getSizeBySide(
                    targetParent.width.toFloat(), targetParent.height.toFloat(), panelSide)
            }
            Association.Group -> {
                val group = parent as ViewGroup
                radius = radiusWeight * GuidelinesInfo.getSizeBySide(
                    group.width.toFloat(), group.height.toFloat(), panelSide)
            }
            Association.Screen -> {
                val w = resources.displayMetrics.widthPixels
                val h = resources.displayMetrics.heightPixels
                radius = radiusWeight * GuidelinesInfo.getSizeBySide(
                    w.toFloat(), h.toFloat(), panelSide)
            }
        }
        hollowCircleDrawable.radius = radius

        hollowCircleDrawable.center.set(targetRect.exactCenterX(), targetRect.exactCenterY())
    }

    private fun layoutMessageView(info: GuidelinesBuilder, left: Int, top: Int, right: Int, bottom: Int) {
        val l: Int
        val t: Int
        val r: Int
        val b: Int
        val radius = hollowCircleDrawable.radius
        val center = hollowCircleDrawable.center
        val boundsLeft = max((center.x - radius).toInt(), left)
        val boundsTop = max((center.y - radius).toInt(), top)
        val boundsRight = min((center.x + radius).toInt(), right)
        val boundsBottom = min((center.y + radius).toInt(), bottom)
        Log.d("Lollipop", "boundsLeft: $boundsLeft, boundsTop: $boundsTop, boundsRight: $boundsRight, boundsBottom: $boundsBottom")
        when (getMessageGravity(info, boundsLeft, boundsTop, boundsRight, boundsBottom)) {
            Location.Left -> {
                l = boundsLeft
                t = boundsTop
                r = targetRect.left
                b = boundsBottom - boundsTop
            }
            Location.Right -> {
                l = targetRect.right
                t = boundsTop
                r = boundsRight - boundsLeft
                b = boundsBottom - boundsTop
            }
            Location.Bottom -> {
                l = boundsLeft
                t = targetRect.bottom
                r = boundsRight - boundsLeft
                b = boundsBottom - targetRect.bottom
            }
            else -> {
                l = boundsLeft
                t = boundsTop
                r = boundsRight - boundsLeft
                b = targetRect.top
            }
        }
        val isFirst = lastMessageBounds.isEmpty
        lastMessageBounds.set(l, t, r, b)
        messageView.layout(l, t, r, b)
        if (isFirst) {
            messageView.requestLayout()
        }
    }

    private fun getMessageGravity(info: GuidelinesBuilder, left: Int, top: Int, right: Int, bottom: Int): Location {
        var gravity = info.messageGravity
        if (gravity == Location.Auto) {
            gravity = Location.Left
            val lSize = (targetRect.left - left) * (bottom - top)
            var s = lSize
            val tSize = (targetRect.top - top) * (right - left)
            if (s < tSize) {
                s = tSize
                gravity = Location.Top
            }
            val rSize = (right - targetRect.right) * (bottom - top)
            if (s < rSize) {
                s = rSize
                gravity = Location.Right
            }
            val bSize = (bottom - targetRect.bottom) * (right - left)
            if (s < bSize) {
                gravity = Location.Bottom
            }
        }
        return gravity
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (animation == animator) {
            animationProcess = animation.animatedValue as Float
            hollowCircleDrawable.animationProgress = animationProcess
        }
    }

    fun show() {
        if (messageView.visibility == View.VISIBLE) {
            messageView.visibility = View.INVISIBLE
        }
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.INVISIBLE
    }

}