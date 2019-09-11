package liang.lollipop.electronicclock.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentColor
import liang.lollipop.electronicclock.utils.ColorPaletteDialog
import liang.lollipop.electronicclock.view.ColorWheelView

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 * 调整项的接口
 */
class AdjustmentColorHolder(view: View): AdjustmentHolder<AdjustmentColor>(view) {

    companion object {
        fun create(inflater: LayoutInflater, group: ViewGroup): AdjustmentColorHolder {
            return AdjustmentColorHolder(
                inflater.inflate(R.layout.item_adjustment_color, group, false))
        }
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summaryView: TextView = view.findViewById(R.id.summaryView)
    private val colorWheelView: ColorWheelView = view.findViewById(R.id.colorWheelView)

    private var bindInfo: AdjustmentColor? = null

    init {
        view.setOnClickListener{ clickView ->
            bindInfo?.let { info ->
                if (!info.enable) {
                    return@setOnClickListener
                }
                ColorPaletteDialog.create(clickView.context) {
                    maxSize = info.maxSize
                    minSize = info.minSize
                    putColors(info.colors)
                }.onColorConfirmed { colors ->
                    colorWheelView.setColors(colors)
                    info.reset(colors)
                    onValueChangeListener?.onValueChange(this, colors)
                }.show()
            }
        }
    }

    override fun onBind(info: AdjustmentColor) {
        bindInfo = info
        setViewEnable(info.enable, titleView, colorWheelView, summaryView)
        titleView.text = info.title
        summaryView.text = info.summary
        colorWheelView.setColors(info.colors)
    }



}