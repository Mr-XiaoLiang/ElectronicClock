package liang.lollipop.electronicclock.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_color_palette.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.view.HuePaletteView
import liang.lollipop.electronicclock.view.SatValPaletteView
import liang.lollipop.electronicclock.view.TransparencyPaletteView

/**
 * 颜色的调色板
 * @author Lollipop
 */
class ColorPaletteDialog private constructor(context: Context) : Dialog(context),
    HuePaletteView.HueCallback, SatValPaletteView.HSVCallback, TransparencyPaletteView.TransparencyCallback{

    companion object {
        fun create(context: Context): ColorPaletteDialog {
            return ColorPaletteDialog(context)
        }
    }

    private val colorArray = ArrayList<Int>()
    private val hsvTemp = FloatArray(3)

    private var colorRGB = Color.RED

    private var colorAlpha = 255

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_color_palette)
        initView()

        val layoutParams = window?.attributes ?: return
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
    }

    private fun initView() {
        huePalette.hueCallback = this
        satValPalette.hsvCallback = this
        transparencyPalette.transparencyCallback = this

        selected(Color.RED)
    }

    fun selected(color: Int) {
        Color.colorToHSV(color,hsvTemp)
        huePalette.parser(hsvTemp[0])
        satValPalette.parser(hsvTemp[1],hsvTemp[2])
        transparencyPalette.parser(Color.alpha(color))
    }

    override fun onHueSelect(hue: Int) {
        satValPalette.onHueChange(hue.toFloat())
    }

    override fun onHSVSelect(hsv: FloatArray, rgb: Int) {
        colorRGB = rgb
        onColorChange()
    }

    override fun onTransparencySelect(alphaF: Float, alphaI: Int) {
        colorAlpha = alphaI
        onColorChange()
    }

    private fun onColorChange() {
        selectedColorView.setStatusColor(merge())
    }

    private fun merge(alpha: Int = colorAlpha, rgb: Int = colorRGB): Int {
        val a = alpha.range(0, 255) shl 24
        return rgb and 0xFFFFFF or a
    }

    private fun Int.range(min: Int, max: Int): Int {
        if (this < min) {
            return min
        }
        if (this > max) {
            return max
        }
        return this
    }

}