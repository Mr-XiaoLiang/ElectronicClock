package liang.lollipop.widget.panel

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-08-04 23:28
 * 空的面板，表示找不到可用的面板类型
 */
class EmptyPanel<T: PanelInfo>(info: T): Panel<T>(info) {
    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return EmptyView(layoutInflater.context)
    }

    private class EmptyView(context: Context): TextView(context) {
        init {
            text = "?"
            gravity = Gravity.CENTER
            setTextColor(Color.GRAY)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
        }
    }

}