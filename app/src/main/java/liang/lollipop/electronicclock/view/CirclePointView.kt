package liang.lollipop.electronicclock.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView
import kotlin.math.min

/**
 * Created by lollipop on 2017/12/21.
 * 状态的显示View
 * @author Lollipop
 */
class CirclePointView(context: Context, attrs: AttributeSet?, defStyleAttr:Int )
    : TextView(context,attrs,defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    private val backgroundDrawable = CircleBgDrawable()

    init {
        if(background is ColorDrawable){
            backgroundDrawable.setColor((background as ColorDrawable).color)
        }
        background = backgroundDrawable
    }

    fun setStatusColor(color:Int){
        backgroundDrawable.setColor(color)
    }

    override fun setBackgroundColor(color: Int) {
        setStatusColor(color)
    }

    class CircleBgDrawable: Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = 0xFFFF3C16.toInt()
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.MITER
            strokeCap = Paint.Cap.BUTT
        }
        private val bound: RectF = RectF()
        private var biggestCorners = true
        private var corners = 0F

        override fun draw(canvas: Canvas) {
            canvas.drawRoundRect(bound, corners, corners, paint)
        }

        override fun setAlpha(i: Int) {
            paint.alpha = i
            invalidateSelf()
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
            invalidateSelf()
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            bound.set(bounds)
            if (biggestCorners) {
                corners = min(bounds.width() * 0.5F, bounds.height() * 0.5F)
            }
            invalidateSelf()
        }

        fun setColor(color: Int) {
            paint.color = color
            invalidateSelf()
        }

    }

}