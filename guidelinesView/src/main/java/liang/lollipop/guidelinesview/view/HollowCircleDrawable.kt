package liang.lollipop.guidelinesview.view

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * @author lollipop
 * @date 2019-08-17 13:34
 * 可以绘制一个空心圆的Drawable
 */
class HollowCircleDrawable: Drawable() {

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    /**
     * 圆形的中心点
     */
    val center = PointF(0F, 0F)

    /**
     * 半径
     */
    var radius = 0F

    /**
     * 背景色
     */
    var backgroundColor = Color.BLACK

    /**
     * 前景色
     */
    var foregroundColor = Color.GREEN

    /**
     * 镂空的位置
     */
    val hollow = RectF()

    /**
     * 动画进度条
     */
    var animationProgress = 1F
        set(value) {
            field = value
            invalidateSelf()
        }

    /**
     * 边距的Float版本
     */
    private var boundsF = RectF()

    override fun draw(canvas: Canvas) {
        val layerId = canvas.saveLayer(boundsF, paint)

        paint.color = backgroundColor
        canvas.drawRect(bounds, paint)
        paint.color = foregroundColor
        canvas.drawCircle(center.x, center.y, radius * animationProgress, paint)

        paint.color = Color.BLACK
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawOval(hollow, paint)

        canvas.restoreToCount(layerId)
        paint.xfermode = null
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        boundsF.set(bounds)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSPARENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

}