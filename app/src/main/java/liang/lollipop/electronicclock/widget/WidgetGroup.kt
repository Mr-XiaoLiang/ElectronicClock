package liang.lollipop.electronicclock.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.math.abs
import kotlin.math.min

/**
 * @author lollipop
 * @date 2019-07-30 20:43
 * 小部件管理器
 */
class WidgetGroup(context: Context, attr: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    FrameLayout(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int): this(context, attr, defStyleAttr, 0)
    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

    companion object {
        private val EMPTY_SIZE = Size(0, 0)
    }

    private val panelList = ArrayList<Panel>()

    private val layoutInflater = LayoutInflater.from(context)

    private var gridSize = EMPTY_SIZE

    var gridCount = 6
        set(value) {
            field = value
            gridSize = EMPTY_SIZE
            requestLayout()
        }

    fun addPanel(panel: Panel) {
        if (panel.view != null) {
            panel.create(layoutInflater, this)
        }
        panelList.add(panel)
        super.addView(panel.view)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val widthSize = right - left - paddingLeft - paddingRight
        val heightSize = bottom - top - paddingTop - paddingBottom
        if (widthSize < 1 || heightSize < 1) {
            // 如果尺寸是空的，那么就放弃本次排版
            return
        }
        if (gridSize == EMPTY_SIZE || gridSize.width < 1 || gridSize.height < 1) {
            // 如果格子尺寸是空的，那么尝试做尺寸计算
            calculateGridSize(widthSize, heightSize)
        }
        val offsetX = paddingLeft
        val offsetY = paddingTop
    }

    private fun calculateGridSize(w: Int, h: Int) {
        val xSize: Int
        val ySize: Int
        // 选择窄的一边作为参照边，进行刻度计算
        // 尽量得到更接近方形的单元格
        if (w < h) {
            xSize = w / gridCount
            ySize = calculateScale(h, xSize)
        } else {
            ySize = h / gridCount
            xSize = calculateScale(w, ySize)
        }
        gridSize = Size(xSize, ySize)
    }

    private fun calculateScale(length: Int, src: Int): Int {
        // 如果长度能够被参照值整除，那么直接返回参照值
        if (length % src == 0) {
            return src
        }
        // 按照参照值计算，得到一个最小的刻度数量
        val minCount = length / src
        // 如果剩余的长度超过了参照值的一半，那么认为剩余过多，那么将刻度数量+1
        // 得到最大刻度数量
        val maxCount = if (length % src > src / 2) {
            minCount + 1
        } else {
            minCount
        }
        // 如果两个刻度值相同，那么认为参照值合法，直接返回
        if (minCount == maxCount) {
            return length / minCount
        }
        // 计算两种刻度数量下的单元格长度
        val minSize = length / minCount
        val maxSize = length / maxCount
        // 当最大刻度数下的单元格剩余长度比默认情况下的少，
        // 并且单元格长度差值没有高于参照值的一半，那么认为新刻度数是合法的，
        // 记为true
        val sizeWeight = length % minSize > length % maxSize && abs(maxSize - src) < src / 2
        if (!sizeWeight) {
            // 如果最大刻度数不合法，那么使用最小刻度数下的单元格长度
            return minSize
        }
        // 最后，选择与参照值差距更小的尺寸作为结果
        return if (abs(maxSize - src) < abs(minSize - src)) {
            maxSize
        } else {
            minSize
        }
    }

    private fun findPanelByView(view: View): Panel {
        for (panel in panelList) {
            if (panel.view == view) {
                return panel
            }
        }
        throw InflateException("Found a view that does not correspond to a panel")
    }

    override fun addView(child: View?) {
        throw InflateException("Can't add view directly")
    }
    override fun addView(child: View?, index: Int) {
        throw InflateException("Can't add view directly")
    }
    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        throw InflateException("Can't add view directly")
    }
    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        throw InflateException("Can't add view directly")
    }
    override fun addView(child: View?, width: Int, height: Int) {
        throw InflateException("Can't add view directly")
    }
    override fun addViewInLayout(child: View?, index: Int, params: ViewGroup.LayoutParams?): Boolean {
        return false
    }
    override fun addViewInLayout(child: View?, index: Int, params: ViewGroup.LayoutParams?, preventRequestLayout: Boolean): Boolean {
        return false
    }

}