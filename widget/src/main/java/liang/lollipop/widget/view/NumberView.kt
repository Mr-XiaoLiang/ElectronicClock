package liang.lollipop.widget.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View

/**
 * @author lollipop
 * @date 2019-07-31 23:41
 * 数字的View，用于展示数字的View
 */
class NumberView(context: Context, attr: AttributeSet?,
                 defStyleAttr: Int, defStyleRes: Int): View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    var numberType = Type.Vector

    // Typeface.createFromAsset(assets, "fonts/time_font.otf")

    enum class Type(val value: Int) {
        Vector(0),
        Font(1)
    }

    init {
//        VectorDrawable
    }

    private class TextDrawable(size: Int): Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            textAlign = Paint.Align.CENTER
        }

        var text: String = ""
            set(value) {
                field = value
                invalidateSelf()
            }

        var textSize = size
            set(value) {
                field = value
                invalidateSelf()
            }

        fun setTypeface(tf: Typeface) {
            if (paint.typeface != tf) {
                paint.typeface = tf
                invalidateSelf()
            }
        }

        override fun draw(canvas: Canvas) {
            drawText(canvas, bounds.exactCenterX(), bounds.exactCenterY())
        }

        private fun drawText(canvas: Canvas, x: Float, y: Float) {
            if (TextUtils.isEmpty(text)) {
                return
            }
            paint.textSize = textSize.toFloat()
            val fm = paint.fontMetrics
            val textY = y - fm.descent + (fm.descent - fm.ascent) / 2
            canvas.drawText(text, x, textY, paint)
        }

        override fun setTintList(tint: ColorStateList?) {
            super.setTintList(tint)
            paint.color = tint?.defaultColor?: Color.BLACK
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity() = PixelFormat.TRANSPARENT

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}