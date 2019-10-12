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
        protected set

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

    /**
     * 是否活跃
     */
    var isActive: Boolean = false
        internal set

    /**
     * 是否是编辑模式
     */
    var isInEditMode: Boolean = false
        internal set

    fun callOnClick(v: View? = null) {
        onClick(v ?: view)
    }

    open fun onClick(v: View?) {

    }

    open fun onAttachedToWindow() {}

    open fun onDetachedFromWindow() {}

    open fun updatePanelInfo(group: WidgetGroup) {}

    fun copyBounds(rect: Rect) {
        rect.set(panelInfo.x, panelInfo.y,
            panelInfo.x + panelInfo.spanX, panelInfo.y + panelInfo.spanY)
    }

    fun copyBoundsByPixels(rect: Rect) {
        rect.set(bounds)
    }

    fun create(layoutInflater: LayoutInflater, parent: WidgetGroup): View {
        val v = onCreateView(layoutInflater, parent)
        view = v
        viewIsInitializer = true
        onViewCreated(v)
        onInfoChange()
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

    /**
     * 当颜色改变时
     */
    open fun onColorChange(color: Int, light: Float) {}

    /**
     * info改变时，触发
     */
    open fun onInfoChange() {}

    /**
     * 当页面激活时
     */
    open fun onPageStart() {}

    /**
     * 当页面停止时
     */
    open fun onPageStop() {}

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

    protected inline fun <reified T> tryMyView(run: (T) -> Unit) {
        view?.tryMyView(run)
    }

    protected inline fun <reified T> View.tryMyView(run: (T) -> Unit) {
        if (this is T) {
            run(this)
        }
    }

    protected fun Int.changeAlpha(alpha: Int): Int {
        return this and 0xFFFFFF or (alpha shl 24)
    }

}