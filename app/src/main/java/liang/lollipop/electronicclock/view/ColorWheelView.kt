package liang.lollipop.electronicclock.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/**
 * 色轮，颜色展示的View
 * @author lollipop
 * @date 2019/09/04
 */
class ColorWheelView(context: Context, attr: AttributeSet?,
                     defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val colorWheelDrawable = ColorWheelDrawable()

    init {
        colorWheelDrawable.callback = this
        if (isInEditMode) {
            setColors(intArrayOf(Color.GRAY, Color.BLUE, Color.CYAN, Color.RED, Color.WHITE, Color.GREEN, Color.LTGRAY))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        colorWheelDrawable.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        colorWheelDrawable.setBounds(left, top, right, bottom)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == colorWheelDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    fun setColors(colors: IntArray) {
        colorWheelDrawable.onColorChanged(colors)
    }

    fun setColors(colors: List<Int>) {
        colorWheelDrawable.onColorChanged(colors)
    }

    private class ColorWheelDrawable: Drawable() {

        private val colorArray = ArrayList<Int>()

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        private val drawBounds = RectF()

        fun onColorChanged(colors: IntArray) {
            colorArray.clear()
            colors.forEach { colorArray.add(it) }
            invalidateSelf()
        }

        fun onColorChanged(colors: List<Int>) {
            colorArray.clear()
            colorArray.addAll(colors)
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            if (colorArray.isEmpty()) {
                return
            }
            val step = 360F / colorArray.size
            for (i in 0 until colorArray.size) {
                paint.color = colorArray[i]
                canvas.drawArc(drawBounds, i * step, step, true, paint)
            }
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            drawBounds.set(bounds)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}