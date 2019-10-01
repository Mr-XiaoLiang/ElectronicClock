package liang.lollipop.electronicclock.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * @author lollipop
 * @date 2019-10-02 00:22
 * 用小点表达数字的View
 */
class NumberPointIcon(context: Context, attrs: AttributeSet?, defStyleAttr:Int )
    : View(context,attrs,defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    private val numberPointDrawable = NumberPointDrawable()

    var color: Int
        set(value) {
            numberPointDrawable.color = value
        }
        get() {
            return numberPointDrawable.color
        }

    var number: Int
        set(value) {
            numberPointDrawable.pointSize = value
        }
        get() {
            return numberPointDrawable.pointSize
        }

    var weight: Float
        set(value) {
            numberPointDrawable.weight = value
        }
        get() {
            return numberPointDrawable.weight
        }

    var maxRadiusWeight: Float
        set(value) {
            numberPointDrawable.maxRadiusWeight = value
        }
        get() {
            return numberPointDrawable.maxRadiusWeight
        }

    init {
        numberPointDrawable.callback = this
        if (isInEditMode) {
            number = 6
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        numberPointDrawable.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        numberPointDrawable.setBounds(left, top, right, bottom)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == numberPointDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    private class NumberPointDrawable: Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        var color: Int
            set(value) {
                paint.color = value
                invalidateSelf()
            }
            get() {
                return paint.color
            }

        private var radius = 0F

        private var pointRadius = 0F

        var pointSize = 0
            set(value) {
                field = value % 10
                invalidateSelf()
            }

        var weight = 1F
            set(value) {
                field = value
                invalidateSelf()
            }

        var maxRadiusWeight = 0.4F
            set(value) {
                field = value
                invalidateSelf()
            }

        override fun draw(canvas: Canvas) {
            // 如果是0，那么画一个圆圈
            if (pointSize == 0) {
                paint.style = Paint.Style.STROKE
                val strokeWidth = radius * 0.1F * weight
                paint.strokeWidth = strokeWidth
                canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(),
                    radius - strokeWidth / 2, paint)
            } else if (pointSize == 1) {
                canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(),
                    pointRadius, paint)
            } else {
                val step = 360F / pointSize
                canvas.save()
                // 移动坐标点到画布中心
                canvas.translate(bounds.exactCenterX(), bounds.exactCenterY())
                paint.style = Paint.Style.FILL
                for (index in 0 until pointSize) {
                    canvas.save()
                    // 旋转角度来保证小点都是在同一个圆上
                    canvas.rotate(step * index - 90F)
                    canvas.drawCircle(radius, 0F, pointRadius, paint)
                    canvas.restore()
                }
                canvas.restore()
            }

        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            // 计算可用半径
            radius = min(bounds.width(), bounds.height()) * 0.5F
            // 计算小点的半径
            pointRadius = radius * getPointRadiusWeight() * weight
            // 缩进半径，保证显示正常
            radius -= pointRadius
            // 发起重绘
            invalidateSelf()
        }

        private fun getPointRadiusWeight(): Float {
            if (pointSize == 0) {
                return 0F
            }
            if (pointSize == 1) {
                return maxRadiusWeight
            }
            val a = 3
            var r = maxRadiusWeight
            for (i in 2..pointSize) {
                r *= (1 - 1F / i / a)
            }
            return r
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
            invalidateSelf()
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
            invalidateSelf()
        }

    }

}