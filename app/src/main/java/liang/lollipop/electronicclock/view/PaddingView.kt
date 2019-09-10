package liang.lollipop.electronicclock.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/**
 * 调整Padding并预览效果的View
 * @author Lollipop
 */
class PaddingView(context: Context, attr: AttributeSet?,
                  defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val paddingDrawable = PaddingDrawable()

    private var target = TouchTarget.None

    private var activeId = 0

    private val touchDown = PointF()

    private val lastTouchLocation = PointF()

    private val redundancy = PointF()

    private var touchWidth = 10F

    init {
        paddingDrawable.callback = this
    }

    private enum class TouchTarget {
        Left,
        Top,
        Right,
        Bottom,
        Full,
        None
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        paddingDrawable.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        paddingDrawable.setBounds(left, top, right, bottom)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == paddingDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 无效的事件交给父类来处理
        event?:return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 当有手指按下时，记录当前手指的id，锁定事件来源
                // 记录按下位置，作为移动距离的凭据
                activeId = event.getPointerId(0)
                touchDown.set(event.x, event.y)
                lastTouchLocation.set(touchDown)
                return checkTouchDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                // 当手指移动的时候，针对移动的距离以及位置来计算边框的位置
                return onTouchMove(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 抬起的时候，再次做最后一次位置移动
                val result = onTouchMove(event)
                // 移动后，结束移动状态
                target = TouchTarget.None
                return result
            }
            MotionEvent.ACTION_POINTER_UP -> {
                // 当多指头情况下，有一个指头抬起来，
                // 那么检查这个指头，是否是锁定的指头
                // 如果是的话，更换为新的指头，并且重置为下一个指头
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activeId) {
                    // 当活跃的那个指头抬起，那么重新选定一个指头作为事件来源
                    val newPointerIndex = if (pointerIndex == 0) {1} else {0}
                    touchDown.set(event.getX(newPointerIndex), event.getY(newPointerIndex))
                    lastTouchLocation.set(touchDown)
                    activeId = event.getPointerId(newPointerIndex)
                }
                return true
            }
        }
        return true
    }

    private fun onTouchMove(event: MotionEvent): Boolean {
        val x = event.getXById()
        val y = event.getYById()
        when (target) {
            TouchTarget.Left -> {
                val newLeft = x.toInt().range(0, bounds.right)
                setPadding(newLeft, paddingTop, paddingRight, paddingBottom)
            }
            TouchTarget.Top -> {
                val newTop = y.toInt().range(0, bounds.bottom)
                setPadding(paddingLeft, newTop, paddingRight, paddingBottom)
            }
            TouchTarget.Right -> {
                val newRight = x.toInt().range(bounds.left, width)
                setPadding(paddingLeft, paddingTop, newRight, paddingBottom)
            }
            TouchTarget.Bottom -> {
                val newBottom = y.toInt().range(bounds.top, height)
                setPadding(paddingLeft, paddingTop, paddingRight, newBottom)
            }
            TouchTarget.Full -> {
                var offsetX = x - lastTouchLocation.x + redundancy.x
                var offsetY = y - lastTouchLocation.y + redundancy.y
                if (offsetX + paddingLeft < 0) {
                    offsetX = 0F - paddingLeft
                }
                if (offsetX + paddingRight > width) {
                    offsetX = width * 1F - paddingRight
                }
                if (offsetY + paddingTop < 0) {
                    offsetY = 0F - paddingTop
                }
                if (offsetY + paddingBottom > height) {
                    offsetY = height * 1F - paddingBottom
                }
                val diffX = offsetX.toInt()
                val diffY = offsetY.toInt()
                redundancy.set(offsetX - diffX, offsetY - diffY)
                setPadding(paddingLeft + diffX, paddingTop + diffY,
                    paddingRight + diffX, paddingBottom + diffY)
            }
            else -> {
                // 当发现事件目标丢失，那么放弃事件
                return false
            }
        }
        lastTouchLocation.set(x, y)
        return true
    }

    /**
     * 检查是否点击在了可触发的点上
     * @return 当返回true时，表示激活拖拽状态
     * 当返回false的时候，表示激活失败
     */
    private fun checkTouchDown(event: MotionEvent): Boolean {
        val x = event.getXById()
        val y = event.getYById()
        val touchRadius = (touchWidth + borderWidth) / 2
        if (x < bounds.left + touchRadius && x > bounds.left - touchRadius
            && y > bounds.top + touchRadius && y < bounds.bottom - touchRadius) {
            target = TouchTarget.Left
            return true
        }
        if (x > bounds.left + touchRadius && x < bounds.right - touchRadius
            && y < bounds.top + touchRadius && y > bounds.top - touchRadius) {
            target = TouchTarget.Top
            return true
        }
        if (x < bounds.right + touchRadius && x > bounds.right - touchRadius
            && y > bounds.top + touchRadius && y < bounds.bottom - touchRadius) {
            target = TouchTarget.Right
            return true
        }
        if (x > bounds.left + touchRadius && x < bounds.right - touchRadius
            && y < bounds.bottom + touchRadius && y > bounds.bottom - touchRadius) {
            target = TouchTarget.Bottom
            return true
        }
        if (bounds.contains(x.toInt(), y.toInt())) {
            target = TouchTarget.Full
            return true
        }
        return false
    }

    private val bounds: Rect
        get() {
            return paddingDrawable.bounds
        }

    private fun Int.range(min: Int, max: Int): Int {
        if (this < min) {
            return min
        }
        if (this > max) {
            return max
        }
        return this
    }

    private fun MotionEvent.getXById(): Float {
        return this.getX(this.findPointerIndex(activeId))
    }

    private fun MotionEvent.getYById(): Float {
        return this.getY(this.findPointerIndex(activeId))
    }

    fun touchWidthDp(value: Float) {
        touchWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            resources.displayMetrics)
    }

    fun borderWidthDp(value: Float) {
        borderWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            resources.displayMetrics)
    }

    fun pointRadiusDp(value: Float) {
        pointRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            resources.displayMetrics)
    }

    var borderWidth: Float
        set(value) {
            paddingDrawable.borderWidth = value
        }
        get() = paddingDrawable.borderWidth

    var pointRadius: Float
        set(value) {
            paddingDrawable.pointRadius = value
        }
        get() = paddingDrawable.pointRadius

    var color: Int
        set(value) {
            paddingDrawable.color = value
        }
        get() = paddingDrawable.color

    private class PaddingDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
        }

        var borderWidth: Float
            set(value) {
                paint.strokeWidth = value
                invalidateSelf()
            }
            get() = paint.strokeWidth

        var pointRadius = 2F
            set(value) {
                field = value
                invalidateSelf()
            }
        var color: Int
            set(value) {
                paint.color = value
                invalidateSelf()
            }
            get() = paint.color

        override fun draw(canvas: Canvas) {
            paint.style = Paint.Style.STROKE
            canvas.drawRect(bounds, paint)
            paint.style = Paint.Style.FILL
            canvas.drawCircle(bounds.exactCenterX(), bounds.top.toFloat(), pointRadius, paint)
            canvas.drawCircle(bounds.exactCenterX(), bounds.bottom.toFloat(), pointRadius, paint)
            canvas.drawCircle(bounds.left.toFloat(), bounds.exactCenterY(), pointRadius, paint)
            canvas.drawCircle(bounds.right.toFloat(), bounds.exactCenterY(), pointRadius, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int = PixelFormat.TRANSPARENT

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }
    }

}