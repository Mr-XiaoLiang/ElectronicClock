package liang.lollipop.widget.widget

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author lollipop
 * @date 2019-07-30 19:41
 * 面板组件的包装类
 */
abstract class Panel<T: PanelInfo>(val panelInfo: T) {

    private val bounds = Rect()

    var viewIsInitializer = false
        private set

    /**
     * 面板的View
     */
    var view: View? = null
        private set

    /**
     * 最小的宽度
     */
    open val minWidth = 0

    /**
     * 最小的高度
     */
    open val minHeight = 0

    val visibility: Int
        get() {
            return view?.visibility ?: View.GONE
        }

    fun copyBounds(rect: Rect) {
        rect.set(panelInfo.x, panelInfo.y,
            panelInfo.x + panelInfo.spanX, panelInfo.y + panelInfo.spanY)
    }

    fun copyBoundsByPixels(rect: Rect) {
        rect.set(bounds)
    }

    fun create(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        val v = onCreateView(layoutInflater, parent)
        view = v
        viewIsInitializer = true
        onViewCreated(v)
        onUpdate()
        return v
    }

    /**
     * 构造方法
     */
    abstract fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View

    /**
     * View构造完成时
     */
    open fun onViewCreated(view: View) {}

    /**
     * 需要更新时
     */
    open fun onUpdate() {}

    fun layout(l: Int, t: Int, r: Int, b: Int) {
        val w = r - l
        val h = b - t
        val layoutChange = w != bounds.width() || h != bounds.height()
        bounds.set(l, t, r, b)
        view?.layout(l, t, r, b)
        if (layoutChange) {
            onSizeChange(w, h)
        }
    }

    fun offset(x: Int, y: Int) {
        bounds.offset(x, y)
        view?.offsetLeftAndRight(x)
        view?.offsetTopAndBottom(y)
    }

    var translationX: Float
        get() { return view?.translationX?:0F }
        set(value) { view?.translationX = value }

    var translationY: Float
        get() { return view?.translationY?:0F }
        set(value) { view?.translationY = value }

    fun saveGridLocation(x: Int, y: Int, spanX: Int, spanY: Int) {
        panelInfo.sizeChange(spanX, spanY)
        panelInfo.offset(x, y)
    }

    /**
     * 当尺寸变更时
     */
    open fun onSizeChange(width: Int, height: Int) {}

}