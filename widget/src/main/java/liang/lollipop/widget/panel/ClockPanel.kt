package liang.lollipop.widget.panel

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.widget.Panel
import java.util.*
import kotlin.math.min

/**
 * @author lollipop
 * @date 2019-07-31 15:19
 * 时钟的面板
 */
class ClockPanel(info: ClockPanelInfo): Panel<ClockPanelInfo>(info) {
    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return ClockView(layoutInflater.context).apply {
            setTypeface(Typeface.createFromAsset(layoutInflater.context.assets, "fonts/liquid_crystal.TTF"))
        }
    }

    override fun onUpdate() {
        super.onUpdate()
        view?.let {
            if (it is ClockView) {
                it.updateTime()
            }
        }
    }

    override fun onInfoChange() {
        super.onInfoChange()
        view?.let {
            if (it is ClockView) {
                if (panelInfo.typeface != null) {
                    it.setTypeface(panelInfo.typeface)
                }
                it.setColor(panelInfo.color)
            }
        }
    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        view?.let {
            if (it is ClockView) {
                it.setColor(color)
            }
        }
    }

    /**
     * 时间显示的View
     */
    private class ClockView(context: Context, attr: AttributeSet?,
        defStyleAttr: Int, defStyleRes: Int) : FrameLayout(context, attr, defStyleAttr, defStyleRes) {

        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
        constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
        constructor(context: Context) : this(context, null)

        companion object {
            private const val MINI_THRESHOLD = 1.4F
            private const val DETAILED_THRESHOLD = 2.5F
            private const val FONT_SIZE = 0.8F
        }

        private val hourView = NumberView(context)
        private val minuteView = NumberView(context)
        private val secondView = NumberView(context)

        private val calendar = Calendar.getInstance()
        private val views = arrayOf(hourView, minuteView, secondView)

        init {
            views.forEach { view ->
                addView(view)
            }
        }

        fun updateTime() {
            calendar.timeInMillis = System.currentTimeMillis()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)
            hourView.text = hour.formatNumber()
            minuteView.text = minute.formatNumber()
            secondView.text = second.formatNumber()
        }

        fun setTypeface(value: Typeface?) {
            views.forEach {
                it.typeface = value
            }
        }

        fun setColor(color: Int) {
            views.forEach {
                it.color = color
            }
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            val width = r - l - paddingLeft - paddingRight
            val height = b - t - paddingTop - paddingBottom
            if (width > height) {
                layoutHorizontal(width, height, paddingLeft, paddingTop)
            } else {
                layoutVertical(width, height, paddingLeft, paddingTop)
            }
        }

        private fun layoutVertical(width: Int, height: Int, left: Int, top: Int) {
            val proportion = 1F * height / width
            if (proportion < MINI_THRESHOLD) {
                layoutByMiniMode(width, height, left, top)
                return
            }
            val isShowSecond = proportion > DETAILED_THRESHOLD
            updateSecondVisibility(isShowSecond)
            val rows = if (isShowSecond) { 3 } else { 2 }
            val viewHeight = min(width * 1F, height * 1F / rows).toInt()
            var y = (height - viewHeight * rows) / 2 + top
            // 小时
            layoutTime(hourView, left, y, width, viewHeight)
            // 分钟
            y += viewHeight
            layoutTime(minuteView, left, y, width, viewHeight)
            // 秒
            if (isShowSecond) {
                y += viewHeight
                layoutTime(secondView, left, y, width, viewHeight)
            }
        }

        private fun layoutHorizontal(width: Int, height: Int, left: Int, top: Int) {
            val proportion = 1F * width / height
            if (proportion < MINI_THRESHOLD) {
                layoutByMiniMode(width, height, left, top)
                return
            }
            val isShowSecond = proportion > DETAILED_THRESHOLD
            updateSecondVisibility(isShowSecond)
            val cols = if (isShowSecond) { 3 } else { 2 }
            val viewWidth = min(height, width / cols)
            var offset = (width - viewWidth * cols) / cols
            if (offset > viewWidth * 0.2F) {
                offset = (viewWidth * 0.2F).toInt()
            }
            var x = ((width - viewWidth * cols - offset * (cols - 1)) / 2 + left)
            // 小时
            layoutTime(hourView, x, top, viewWidth, height)
            // 分钟
            x += offset
            x += viewWidth
            layoutTime(minuteView, x, top, viewWidth, height)
            // 秒
            if (isShowSecond) {
                x += offset
                x += viewWidth
                layoutTime(secondView, x, top, viewWidth, height)
            }
        }

        private fun layoutTime(tensView: NumberView, x: Int, y: Int, viewWidth: Int, viewHeight: Int) {
            tensView.textSize = getFontSizeByWidth(viewWidth, viewHeight).toInt()
            tensView.layout(x, y, x + viewWidth, y + viewHeight)
        }

        private fun getFontSizeByWidth(viewWidth: Int, viewHeight: Int): Float {
            val size = viewHeight * FONT_SIZE
            if (size > viewWidth) {
                return viewWidth.toFloat()
            }
            return size
        }

        private fun updateSecondVisibility(isShow: Boolean) {
            secondView.visibility = if (isShow) { View.VISIBLE } else { View.GONE }
        }

        private fun layoutByMiniMode(width: Int, height: Int, left: Int, top: Int) {
            updateSecondVisibility(false)
            val viewHeight = height / 2
            var y = top
            // 小时
            layoutTime(hourView, left, y, width, viewHeight)
            // 分钟
            y += viewHeight
            layoutTime(minuteView, left, y, width, viewHeight)
        }

        private fun Int.formatNumber(): String {
            return if (this < 10) {
                "0$this"
            } else {
                "$this"
            }
        }

    }

    private class NumberView(context: Context): View(context) {

        private val textDrawable = TextDrawable()

        init {
            textDrawable.callback = this
        }

        var text: String
            set(value) {
                textDrawable.text = value
            }
            get() {
                return textDrawable.text
            }

        var textSize: Int
            set(value) {
                textDrawable.textSize = value
            }
            get() {
                return textDrawable.textSize
            }

        var color: Int
            get() = textDrawable.color
            set(value) {
                textDrawable.color = value
            }

        var typeface: Typeface?
            get() = textDrawable.typeface
            set(value) {
                textDrawable.typeface = value
            }

        var bold: Boolean
            set(value) {
                textDrawable.bold = value
            }
            get() {
                return textDrawable.bold
            }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            canvas?:return
            textDrawable.draw(canvas)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            val left = paddingLeft
            val top = paddingTop
            val right = width - paddingRight
            val bottom = height - paddingBottom
            textDrawable.setBounds(left, top, right, bottom)
        }

        override fun invalidateDrawable(drawable: Drawable) {
            super.invalidateDrawable(drawable)
            if (drawable == textDrawable) {
                invalidate()
                invalidateOutline()
            }

        }
    }

    private class TextDrawable: Drawable() {

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

        var textSize = 10
            set(value) {
                field = value
                invalidateSelf()
            }

        var color: Int
            set(value) {
                paint.color = value
                invalidateSelf()
            }
            get() {
                return paint.color
            }

        var typeface: Typeface?
            get() = paint.typeface
            set(value) {
                paint.typeface = value
                invalidateSelf()
            }

        var bold: Boolean
            set(value) {
                paint.isFakeBoldText = value
                invalidateSelf()
            }
            get() {
                return paint.isFakeBoldText
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