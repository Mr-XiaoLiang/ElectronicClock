package liang.lollipop.electronicclock.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.TintUtil
import kotlin.math.abs

/**
 * @author lollipop
 * @date 2020-01-12 00:17
 * 数字的View
 */
class NumberView(context: Context, attr: AttributeSet?,
                 defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    enum class Number(val value: Int) {
        Number0(0),
        Number1(1),
        Number2(2),
        Number3(3),
        Number4(4),
        Number5(5),
        Number6(6),
        Number7(7),
        Number8(8),
        Number9(9),
        Colon(10)
    }

    private val numberDrawableArray: Array<Drawable?> = Array(Number.values().size) { null }

    val drawableResIds = arrayOf(R.drawable.ic_black_num0, R.drawable.ic_black_num1,
        R.drawable.ic_black_num2, R.drawable.ic_black_num3, R.drawable.ic_black_num4,
        R.drawable.ic_black_num5, R.drawable.ic_black_num6, R.drawable.ic_black_num7,
        R.drawable.ic_black_num8, R.drawable.ic_black_num9, R.drawable.ic_black_colon)

    private var numberDrawable: Drawable? = null

    private var number = Number.Number0

    private var tintColor: ColorStateList = ColorStateList.valueOf(Color.BLACK)
    private var tintMode: PorterDuff.Mode? = null
    private var tintBlendMode: BlendMode? = null

    init {
        updateNumber(Number.Number0)
    }

    fun setColor(color: Int) {
        foregroundTintList = ColorStateList.valueOf(color)
    }

    fun updateNumber(num: Int) {
        val i = abs(num) % 10
        if (number.value == i) {
            return
        }
        val target = when(i) {
            0 -> Number.Number0
            1 -> Number.Number1
            2 -> Number.Number2
            3 -> Number.Number3
            4 -> Number.Number4
            5 -> Number.Number5
            6 -> Number.Number6
            7 -> Number.Number7
            8 -> Number.Number8
            9 -> Number.Number9
            else -> Number.Number0
        }
        updateNumber(target)
    }

    fun showColon() {
        updateNumber(Number.Colon)
    }

    private fun updateColor() {
        numberDrawable?.let {
            val tint = TintUtil.tintDrawable(it).setColor(tintColor)
            tintMode?.let {mode -> tint.setMode(mode) }
            tint.tint()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tintBlendMode?.let { mode -> it.setTintBlendMode(mode) }
            }
        }
    }

    private fun updateNumber(num: Number) {
        number = num
        var drawable = numberDrawableArray[num.value]
        if (drawable == null) {
            drawable = newDrawable(num)
            numberDrawableArray[num.value] = drawable
        }
        val needLayout = numberDrawable?.sameShape(drawable) != true
        numberDrawable?.callback = null
        numberDrawable = drawable
        numberDrawable?.callback = this
        updateDrawableSize()
        updateColor()
        if (needLayout) {
            requestLayout()
        } else {
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        numberDrawable?.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateDrawableSize()
    }

    private fun updateDrawableSize() {
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        numberDrawable?.setBounds(left, top, right, bottom)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == numberDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    private fun newDrawable(num: Number): Drawable {
        val resId = drawableResIds[num.value]
        return context.getDrawable(resId)?.mutate()?:throw RuntimeException("Drawable not found")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        // 两者如果类型一样，那么没办法做比例计算，放弃它
        if (widthMode == heightMode) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            val weight = getDrawableWeight()
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSize = (widthSize / weight).toInt()
            setMeasuredDimension(widthSize, heightSize)
            return
        }
        if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            val weight = getDrawableWeight()
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
            val widthSize = (heightSize * weight).toInt()
            setMeasuredDimension(widthSize, heightSize)
            return
        }
        // 如果不满足要求，那么放弃它
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun getDrawableWeight(): Float {
        return findActiveDrawable().getWeight()
    }

    private fun Drawable.getWeight(): Float {
        val w = this.intrinsicWidth
        val h = this.intrinsicHeight
        if (w < 0 || h < 0) {
            return 1F
        }
        return w * 1F / h
    }

    private fun Drawable.sameShape(drawable: Drawable): Boolean {
        val thisWeight = (this.getWeight() * 1000).toInt()
        val otherWeight = (drawable.getWeight() * 1000).toInt()
        return thisWeight == otherWeight
    }

    private fun findActiveDrawable(): Drawable {
        if (numberDrawable != null) {
            return numberDrawable!!
        }
        for (drawable in numberDrawableArray) {
            if (drawable != null) {
                return drawable
            }
        }
        val drawable = newDrawable(Number.Number0)
        numberDrawableArray[Number.Number0.value] = drawable
        return drawable
    }

    override fun setForegroundTintList(tint: ColorStateList?) {
        tintColor = tint ?: ColorStateList.valueOf(Color.BLACK)
        updateColor()
    }

    override fun setForegroundTintMode(tint: PorterDuff.Mode?) {
        tintMode = tint
        updateColor()
    }

    override fun setForegroundTintBlendMode(blendMode: BlendMode?) {
        tintBlendMode = blendMode
        updateColor()
    }

}