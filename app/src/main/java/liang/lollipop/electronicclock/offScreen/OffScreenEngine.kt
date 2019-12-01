package liang.lollipop.electronicclock.offScreen

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import kotlin.math.max

/**
 * @author lollipop
 * @date 2019-11-26 22:47
 * 离屏渲染引擎
 */
class OffScreenEngine(private val painter: Painter) {

    companion object {
        var DEF_FPS = 60
    }

    private var frameInterval = 1000L / DEF_FPS

    var fps = DEF_FPS
        set(value) {
            field = value
            frameInterval = if (value <= 0) {
                0
            } else {
                1000L / value
            }
        }

    private var surface: Surface? = null

    private var viewSize = ViewSize(0, 0)

    private var padding = Inset(0, 0, 0, 0)

    private var isStop = false

    private var isDestroy = false

    private var isShown = false

    private val tmpRect = Rect()

    private var isPersistence = false

    private val handlerThread: HandlerThread by lazy {
        HandlerThread("OffScreenEngine")
    }

    private val handler: Handler by lazy {
        Handler(handlerThread.looper)
    }

    private val drawingTask = Runnable {
        if (isStop) {
            return@Runnable
        }
        val startTime = System.currentTimeMillis()
        if (viewSize.isEmpty || surface == null) {
            nextFrame(startTime)
            return@Runnable
        }
        surface?.let { s ->
            val canvas = s.lockCanvas(tmpRect)
            draw(canvas)
            s.unlockCanvasAndPost(canvas)
        }
        if (isPersistence) {
            surface?.let { s ->
                val canvas = s.lockCanvas(tmpRect)
                draw(canvas)
                s.unlockCanvasAndPost(canvas)
            }
            isPersistence = false
            return@Runnable
        }
        nextFrame(startTime)
    }

    init {
        painter.setInvalidateCallback(object : InvalidateCallback {
            override fun requestInvalidate() {
                nextFrame()
            }

            override fun drawingEnd() {
                isPersistence = true
                nextFrame()
            }
        })
    }

    private fun nextFrame(startTime: Long = 0L) {
        if (isStop) {
            return
        }
        pauseDraw()
        val delayed = if (startTime == 0L || frameInterval == 0L) {
            0L
        } else  {
            val now = System.currentTimeMillis()
            frameInterval - now + startTime
        }
        handler.postDelayed(drawingTask, max(delayed, 0))
    }

    private fun pauseDraw() {
        handler.removeCallbacks(drawingTask)
    }

    fun setSize(width: Int, height: Int) {
        viewSize.reset(width, height)
        tmpRect.set(0, 0, width, height)
        painter.onSizeChange(viewSize.width, viewSize.height)
    }

    private fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        padding.reset(left, top, right, bottom)
        painter.onInsetChange(left, top, right, bottom)
    }

    fun draw(canvas: Canvas) {
        painter.draw(canvas)
    }

    fun onShow() {
        if (isDestroy) {
            throw RuntimeException("OffScreenEngine is DESTROY")
        }
        isShown = true
        painter.onShow()
        if (!isStop) {
            nextFrame()
        }
    }

    fun onHide() {
        isShown = false
        painter.onHide()
        pauseDraw()
    }

    fun stop() {
        isStop = true
        pauseDraw()
    }

    fun start() {
        if (isDestroy) {
            throw RuntimeException("OffScreenEngine is DESTROY")
        }
        isStop = false
        if (isShown) {
            nextFrame()
        }
    }

    fun destroy() {
        stop()
        isDestroy = true
    }



}