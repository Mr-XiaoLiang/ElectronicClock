package liang.lollipop.electronicclock.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import liang.lollipop.guidelinesview.util.lifecycleBinding
import liang.lollipop.guidelinesview.util.onEnd

/**
 * @date: 2019/02/07 18:32
 * @author: lollipop
 * 一个不显眼的加载提示View
 */
class ContentLoadingProgressBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    View(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    companion object {
        /**
         * ms
         * 最小显示时间，避免一闪而过导致用户体验的损失
         */
        private const val MIN_SHOW_TIME = 2000L
        /**
         * ms
         * 最小延迟时间，显示延迟，
         * 为了避免过快的加载完成而导致一闪而过的显示
         */
        private const val MIN_DELAY = 500L

        /**
         * 动画时间
         */
        private const val ANIMATOR_DURATION = 300L

        private const val TAG = "ContentLoadingProgressBar"

        /**
         * 默认的加载动画时间
         */
        private const val DEF_LOADING_DURATION = 2300L
    }

    /**
     * 隐藏的延时任务
     * 用于满足最小显示时间的要求
     * 当最小时间满足时，执行关闭操作
     */
    private val delayedHide = Runnable {
        postedHide = false
        startTime = -1
        onShown = false
        animationToHide()
    }

    /**
     * 显示的延时任务
     * 用于延时显示View，避免快速的显示关闭造成的不良体验
     */
    private val delayedShow = Runnable {
        postedShow = false
        if (!dismissed) {
            onShown = true
            startTime = System.currentTimeMillis()
            animationToShow()
        }
    }

    /**
     * 启动时间，用于记录开始显示时间
     * 便于计算View的显示时间
     */
    private var startTime = -1L

    /**
     * 用于记录是否发起了关闭请求
     */
    private var postedHide = false

    /**
     * 用于记录是否发起了显示请求
     */
    private var postedShow = false

    /**
     * 用于记录是否已经关闭
     */
    private var dismissed = false

    /**
     * 是否已经显示
     */
    private var onShown = false

    private val progressDrawable = ProgressDrawable()

    init {
        if (isInEditMode) {
            progressDrawable.putColor(Color.BLACK)
            progressDrawable.progress(70, 100)
        } else {
            hideView()
        }
        progressDrawable.animatorDuration(DEF_LOADING_DURATION)
        progressDrawable.callback = this
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == progressDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        progressDrawable.setBounds(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        progressDrawable.draw(canvas)
    }

    /**
     * 显示方法
     * 触发后将会开启一个延时任务用于延时显示View
     */
    fun show() {
        // 避免反复显示进入动画
        if (onShown) {
            return
        }
        logD("show() -> postedShow: $postedShow")
        // 重置启动时间
        startTime = -1
        dismissed = false
        removeCallbacks(delayedHide)
        postedHide = false
        // 如果已经开启了显示状态，那么不做重复的事情
        if (!postedShow) {
            postDelayed(delayedShow, MIN_DELAY)
            postedShow = true
        }
    }

    /**
     * 隐藏方法
     * 触发后将会判断场景来关闭显示
     * 如果已经显示，那么将会在满足最小显示时间之后关闭
     * 如果没有显示，那么将会直接停止显示任务
     */
    fun hide() {
        logD("hide()")
        dismissed = true
        removeCallbacks(delayedShow)
        postedShow = false
        val diff = System.currentTimeMillis() - startTime
        if (startTime < 0) {
            logD("hide() -> startTime < 0")
            hideView()
        } else if (diff >= MIN_SHOW_TIME) {
            logD("hide() -> diff >= MIN_SHOW_TIME")
            animationToHide()
        } else {
            logD("hide() -> postedHide")
            // 如果已经发送了一次请求，那么不再重复发起
            if (!postedHide) {
                postDelayed(delayedHide, MIN_SHOW_TIME - diff)
                postedHide = true
            }
        }
    }

    /**
     * 通过动画隐藏View
     */
    private fun animationToHide() {
        logD("animationToHide()")
        animate().setDuration(ANIMATOR_DURATION)
            .scaleX(0F)
            .scaleY(0F)
            .lifecycleBinding {
                onEnd {
                    logD("onAnimationEnd()")
                    hideView()
                    removeThis(it)
                }
            }.start()
    }

    /**
     * 通过动画显示View
     */
    private fun animationToShow() {
        logD("animationToShow()")
        showView()
        scaleX = 0F
        scaleY = 0F
        animate().setDuration(ANIMATOR_DURATION)
            .scaleX(1F)
            .scaleY(1F)
            .start()
    }

    /**
     * 隐藏View的方法
     */
    private fun hideView() {
        logD("hideView()")
        visibility = View.GONE
        progressDrawable.stop()
        onShown = false
    }

    /**
     * 显示View的方法
     */
    private fun showView() {
        logD("showView()")
        visibility = View.VISIBLE
        progressDrawable.start()
        onShown = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        logD("onAttachedToWindow()")
        animate().cancel()
        removeCallbacks()
        progressDrawable.stop()
        if (postedShow) {
            logD("onAttachedToWindow() -> show()")
            postedShow = false
            show()
            return
        }
        logD("onAttachedToWindow() -> hideView()")
        postedHide = false
        hideView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        logD("onDetachedFromWindow()")
        animate().cancel()
        removeCallbacks()
        progressDrawable.stop()
        postedShow = false
        postedHide = false
        dismissed = false
        startTime = -1
    }

    /**
     * 移除全部的回调函数
     */
    private fun removeCallbacks() {
        logD("removeCallbacks()")
        removeCallbacks(delayedHide)
        removeCallbacks(delayedShow)
    }

    private fun logD(value: String) {
        Log.d("Lollipop", "$TAG: $value")
    }

    fun putColorForRes(vararg ids: Int) {
        val colors = IntArray(ids.size)
        for (index in 0 until ids.size) {
            colors[index] = ContextCompat.getColor(context, ids[index])
        }
        putColor(*colors)
    }

    fun putColor(vararg colors: Int) {
        progressDrawable.putColor(*colors)
    }

    fun loadingDuration(value: Long) {
        progressDrawable.animatorDuration(value)
    }

    class ProgressDrawable: Drawable(), Animatable, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
//        strokeCap = Paint.Cap.ROUND
        }

        private val colorList = ArrayList<Int>()

        private var colorIndex = 0

        private var radius = 0F

        private var strokeWidthWeight = 0.25F

        private var startAngle = 0F

        private var sweepAngle = 0F

        private var ovalBounds = RectF()

        private var rotateStep = 360F

        private var sweepStep = 300F

        private var lastAnimationProgress = 0F

        private var lastStep = 0F

        companion object {
            private const val TAG = "ProgressDrawable"
        }

        private val animator = ValueAnimator.ofFloat(0F, 2F).apply {
            addUpdateListener(this@ProgressDrawable)
            addListener(this@ProgressDrawable)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
        }

        override fun draw(canvas: Canvas) {
            if (colorList.isEmpty() || radius < 1) {
                return
            }
//        logD("draw() -> startAngle: $startAngle, sweepAngle: $sweepAngle")
            canvas.drawArc(ovalBounds, startAngle, sweepAngle, false, paint)
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            if (bounds == null) {
                return
            }
            radius = Math.min(bounds.width(), bounds.height()) * 0.5F
            val strokeWidth = radius * strokeWidthWeight
            radius -= strokeWidth * 0.5F
            paint.strokeWidth = strokeWidth
            val top = bounds.exactCenterY() - radius
            val left = bounds.exactCenterX() - radius
            val right = bounds.exactCenterX() + radius
            val bottom = bounds.exactCenterY() + radius
            ovalBounds.set(left, top, right, bottom)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity() = PixelFormat.TRANSPARENT

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        fun putColor(vararg colors: Int) {
            colorList.clear()
            for (color in colors) {
                colorList.add(color)
            }
        }

        fun progress(progress: Int, max: Int) {
            stop()
            paint.color = colorList[0]
            startAngle = 0F
            sweepAngle = 360F * progress / max
            invalidateSelf()
        }

        fun animatorDuration(value: Long) {
            animator.duration = value
        }

        override fun isRunning(): Boolean {
            return animator.isRunning
        }

        override fun start() {
            if (colorList.isEmpty()) {
                animator.cancel()
                return
            }
            animator.start()
            invalidateSelf()
        }

        override fun stop() {
            animator.cancel()
        }

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            if (animation == animator) {
                val value = animator.animatedValue as Float
//            logD("onAnimationUpdate($value)")
                val isShrink = value > 1
                val sweepValue = if (value > 1) { 2 - value } else { value }
                val sweepLength = sweepStep * sweepValue
                val startValue = if (value < lastAnimationProgress) {
                    2F - lastAnimationProgress + value
                } else {
                    value - lastAnimationProgress
                }
                lastAnimationProgress = value

                val step = startValue * rotateStep
                startAngle += step
                if (step < 1) {
                    startAngle += lastStep
                } else {
                    lastStep = step
                }

                if (isShrink) {
                    val diff = sweepAngle - sweepLength
                    startAngle += diff
                }
                sweepAngle = sweepLength
                startAngle %= 360

                invalidateSelf()
            }
        }

        override fun onAnimationRepeat(animation: Animator?) {
            colorIndex ++
            colorIndex %= colorList.size
            paint.color = colorList[colorIndex]
//        logD("onAnimationRepeat($colorIndex)")
        }

        override fun onAnimationEnd(animation: Animator?) {}

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {
            colorIndex ++
            colorIndex %= colorList.size
            paint.color = colorList[colorIndex]
            logD("onAnimationStart($colorIndex)")
        }

        private fun logD(value: String) {
            Log.d("Lollipop", "$TAG: $value")
        }

    }

}