package liang.lollipop.electronicclock.widget.panel

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.BatteryManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.activity.PanelInfoAdjustmentActivity
import liang.lollipop.electronicclock.widget.info.BatteryPanelInfo
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo
import kotlin.math.min


/**
 * @author lollipop
 * @date 2019-08-19 22:15
 * 电池信息的展示面板
 */
class BatteryPanel(info: BatteryPanelInfo): Panel<BatteryPanelInfo>(info), View.OnClickListener {

    private val batteryDrawable = BatteryDrawable(info)
    private var batteryManager: BatteryManager? = null

    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return createView(layoutInflater.context)
    }

    fun createView(context: Context): View {
        batteryManager = context.getSystemService(BATTERY_SERVICE) as? BatteryManager
        return View(context).apply {
            background = batteryDrawable
            setOnClickListener(this@BatteryPanel)
        }
    }

    override fun onInfoChange() {
        batteryDrawable.onInfoChange(panelInfo)
    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        batteryDrawable.defColor = color
//        batteryDrawable.alpha = if (panelInfo.colorArray.isEmpty()) {
//            255
//        } else {
//            (light * 255).toInt()
//        }
    }

    override fun onClick(v: View?) {
        v?:return
        val context = v.context
        if (panelInfo.id == PanelInfo.NO_ID) {
            Snackbar.make(v, R.string.alert_save_first, Snackbar.LENGTH_LONG).show()
            return
        }
        context?.startActivity(PanelInfoAdjustmentActivity.getIntent(panelInfo))
    }

    override fun onUpdate() {
        super.onUpdate()
        val manager = batteryManager?:return
        //当前电量百分比
        val value = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) * 0.01F
        batteryDrawable.progress = value
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
            strokeCap = Paint.Cap.ROUND
        }

        var progress = 0.7F
            set(value) {
                field = value
                invalidateSelf()
            }

        private var shader: Shader? = null

        private val borderPath = Path()

        private val tmpPath = Path()

        private var corner = 0F

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

            corner = min(batteryBounds.width(), batteryBounds.height()) / 2 * info.corner

            borderPath.reset()
            val r = min(batteryBounds.width(), batteryBounds.height()) / 2
            var borderWidth = r * info.borderWidth
            if (borderWidth > r / 2) {
                borderWidth = r / 2
            }
            if (info.isArc) {
                borderPath.addCircle(batteryBounds.centerX(), batteryBounds.centerY(), r, Path.Direction.CW)

                tmpPath.reset()
                tmpPath.addCircle(batteryBounds.centerX(), batteryBounds.centerY(), r - borderWidth, Path.Direction.CW)

                borderPath.op(tmpPath, Path.Op.DIFFERENCE)
            } else {
                borderPath.addRoundRect(batteryBounds, corner, corner, Path.Direction.CW)

                tmpPath.reset()
                tmpPath.addRoundRect(batteryBounds.left + borderWidth, batteryBounds.top + borderWidth,
                    batteryBounds.right - borderWidth, batteryBounds.bottom - borderWidth,
                    corner, corner, Path.Direction.CW)

                borderPath.op(tmpPath, Path.Op.DIFFERENCE)
            }

            if (info.colorArray.size > 0) {
                val colorArray = IntArray(info.colorArray.size + 1)
                for (i in colorArray.indices) {
                    colorArray[i] = info.colorArray[i % info.colorArray.size]
                }
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
            invalidateSelf()
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            relayout()
        }

        override fun draw(canvas: Canvas) {
            // 绘制背景
            if (info.isShowBg) {
                drawProgress(canvas, 1F, (drawAlpha * 0.2F).toInt())
            }
            // 电池本体
            drawProgress(canvas, progress)
            // 绘制边框
            if (info.isShowBorder) {
                drawBorder(canvas)
            }
        }

        private fun drawProgress(canvas: Canvas, pro: Float, alpha: Int = drawAlpha) {
            paint.color = defColor
            paint.shader = shader
            paint.alpha = alpha
            val left: Float
            val top: Float
            val right: Float
            val bottom: Float
            // 环形模式
            var borderInset = 0F
            val r = min(batteryBounds.width(), batteryBounds.height()) / 2
            if (info.isShowBorder) {
                borderInset = r * info.borderWidth
                if (borderInset > r / 2) {
                    borderInset = r / 2
                }
            }
            if (info.isArc) {
                var strokeWidth = r * info.arcWidth
                if (strokeWidth > r / 2) {
                    strokeWidth = r / 2
                }

                left = batteryBounds.centerX() - r + strokeWidth / 2 + borderInset
                top = batteryBounds.centerY() - r + strokeWidth / 2 + borderInset
                right = batteryBounds.centerX() + r - strokeWidth / 2 - borderInset
                bottom = batteryBounds.centerY() + r - strokeWidth / 2 - borderInset

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                canvas.drawArc(left, top, right, bottom,
                    -90F, 360 * pro, false, paint)
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeWidth = 0F
            } else {
                // 矩形模式，需要判断方向
                left = batteryBounds.left
                bottom = batteryBounds.bottom
                if (info.isVertical) {
                    top = batteryBounds.top + batteryBounds.height() * (1 - pro)
                    right = batteryBounds.right
                } else {
                    top = batteryBounds.top
                    right = batteryBounds.left + batteryBounds.width() * pro
                }
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeWidth = 0F
                canvas.drawRoundRect(left + borderInset, top + borderInset,
                    right - borderInset, bottom - borderInset,
                    corner, corner, paint)
            }
            paint.shader = null
        }

        private fun drawBorder(canvas: Canvas) {
            paint.color = info.borderColor
            paint.alpha = drawAlpha
            canvas.drawPath(borderPath, paint)
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