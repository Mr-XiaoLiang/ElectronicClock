package liang.lollipop.electronicclock.widget.panel

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.electronicclock.widget.info.BatteryPanelInfo
import liang.lollipop.widget.widget.Panel
import kotlin.math.min

/**
 * @author lollipop
 * @date 2019-08-19 22:15
 * 电池信息的展示面板
 */
class BatteryPanel(info: BatteryPanelInfo): Panel<BatteryPanelInfo>(info) {

    private val batteryDrawable = BatteryDrawable(info)

    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return createView(layoutInflater.context)
    }

    fun createView(context: Context): View {
        return View(context).apply {
            background = batteryDrawable
        }
    }

    override fun onInfoChange() {
        batteryDrawable.onInfoChange(panelInfo)
    }

    private class BatteryDrawable(private var info: BatteryPanelInfo): Drawable() {

        private val batteryBounds = RectF()

        private var drawAlpha = 255

        var defColor = Color.WHITE
            set(value) {
                field = value
                invalidateSelf()
            }

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        var progress = 1F
            set(value) {
                field = value
                invalidateSelf()
            }

        private var shader: Shader? = null

        fun onInfoChange(newInfo: BatteryPanelInfo) {
            info = newInfo
            relayout()
        }

        private fun relayout() {
            if (bounds.isEmpty) {
                return
            }
            batteryBounds.set(bounds)
            val padding = info.padding
            batteryBounds.left += padding[0] * bounds.width()
            batteryBounds.top += padding[1] * bounds.height()
            batteryBounds.right -= padding[2] * bounds.width()
            batteryBounds.bottom -= padding[3] * bounds.height()

            val colorArray = IntArray(info.colorArray.size)
            for (i in 0 until  info.colorArray.size) {
                colorArray[i] = info.colorArray[i]
            }

            if (info.colorArray.size > 1) {
                shader = if (info.isArc) {
                    SweepGradient(batteryBounds.centerX(), batteryBounds.centerY(), colorArray, null)
                } else {
                    val sX: Float
                    val sY: Float
                    val eX: Float
                    val eY: Float
                    if (info.isVertical) {
                        sX = batteryBounds.centerX()
                        sY = batteryBounds.top
                        eX = sX
                        eY = batteryBounds.bottom
                    } else {
                        sX = batteryBounds.left
                        sY = batteryBounds.centerY()
                        eX = batteryBounds.right
                        eY = sY
                    }
                    LinearGradient(sX, sY, eX, eY, colorArray, null, Shader.TileMode.CLAMP)
                }
            } else {
                shader = null
            }
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            relayout()
        }

        override fun draw(canvas: Canvas) {
            paint.alpha = drawAlpha
            // 绘制背景
            if (info.isShowBg) {
                paint.alpha = (drawAlpha * 0.5F).toInt()
                drawProgress(canvas)
                paint.alpha = drawAlpha
            }
            // 电池本体
            drawProgress(canvas)
            // 绘制边框
            if (info.isShowBorder) {
                drawBorder(canvas)
            }
        }

        private fun drawProgress(canvas: Canvas) {
            paint.color = defColor
            paint.shader = shader
            val left: Float
            val top: Float
            val right: Float
            val bottom: Float
            // 环形模式
            if (info.isArc) {
                val r = min(batteryBounds.width(), batteryBounds.height()) / 2
                var strokeWidth = r * info.arcWidth
                if (strokeWidth > r / 2) {
                    strokeWidth = r / 2
                }

                left = batteryBounds.centerX() - r - strokeWidth
                top = batteryBounds.centerY() - r - strokeWidth
                right = batteryBounds.centerX() + r + strokeWidth
                bottom = batteryBounds.centerY() + r + strokeWidth

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                canvas.drawArc(left, top, right, bottom,
                    -90F, 360 * progress, false, paint)
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeWidth = 0F
            } else {
                // 矩形模式，需要判断方向
                left = batteryBounds.left
                bottom = batteryBounds.bottom
                if (info.isVertical) {
                    top = batteryBounds.top + batteryBounds.height() * progress
                    right = batteryBounds.right
                } else {
                    top = batteryBounds.top
                    right = batteryBounds.left + batteryBounds.width() * progress
                }
                val corner = min(batteryBounds.width(), batteryBounds.height()) / 2 * info.corner

                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeWidth = 0F
                canvas.drawRoundRect(left, top, right, bottom, corner, corner, paint)
            }
            paint.shader = null
        }

        private fun drawBorder(canvas: Canvas) {
            paint.color = info.borderColor
            if (info.isArc) {
                val r = min(batteryBounds.width(), batteryBounds.height()) / 2
                var strokeWidth = r * info.borderWidth
                if (strokeWidth > r / 2) {
                    strokeWidth = r / 2
                }

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                canvas.drawCircle(batteryBounds.centerX(), batteryBounds.centerY(), r - strokeWidth / 2, paint)
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeWidth = 0F
            } else {
                val strokeWidth = min(batteryBounds.width(), batteryBounds.height()) * info.borderWidth
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                val retract = strokeWidth / 2
                val left = batteryBounds.left + retract
                val top = batteryBounds.top + retract
                val right = batteryBounds.right - retract
                val bottom = batteryBounds.bottom - retract
                val corner = min(right - left, bottom - top) / 2 * info.corner
                canvas.drawRoundRect(left, top, right, bottom, corner, corner, paint)
            }
        }

        override fun setAlpha(alpha: Int) {
            drawAlpha = alpha
            paint.alpha = alpha
            invalidateSelf()
        }

        override fun getOpacity() = PixelFormat.TRANSPARENT

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }
    }

}