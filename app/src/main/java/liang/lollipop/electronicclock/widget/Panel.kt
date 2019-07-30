package liang.lollipop.electronicclock.widget

import android.graphics.Rect
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author lollipop
 * @date 2019-07-30 19:41
 * 面板组件的包装类
 */
abstract class Panel(val panelInfo: PanelInfo) {

    private val bounds = Rect()

    companion object {
        val EMPTY_SIZE = Size(0, 0)
    }

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

    fun copyBounds(rect: Rect) {
        rect.set(bounds)
    }

    fun create(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        val v = onCreateView(layoutInflater, parent)
        view = v
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
    open fun onViewCreated(view: View) {

    }

    /**
     * 需要更新时
     */
    open fun onUpdate() {

    }

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

    /**
     * 当尺寸变更时
     */
    open fun onSizeChange(width: Int, height: Int) {}

}