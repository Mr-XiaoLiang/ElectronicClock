package liang.lollipop.electronicclock.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_info.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentBoolean
import liang.lollipop.electronicclock.bean.AdjustmentColor
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.bean.AdjustmentInteger
import liang.lollipop.electronicclock.list.AdjustmentAdapter
import liang.lollipop.electronicclock.utils.doAsync
import liang.lollipop.electronicclock.utils.uiThread
import liang.lollipop.widget.utils.DatabaseHelper
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-08-25 15:23
 * 面板信息调整的Fragment
 */
abstract class PanelInfoAdjustmentFragment: Fragment() {

    companion object {
        const val ARG_INFO_ID = "ARG_INFO_ID"

        private const val TAG = "PanelInfoAdjustmentFragment"
    }

    private var infoId = ""

    private var infoLoadCallback: InfoLoadCallback? = null

    private val logE = Utils.loggerE(TAG)

    private val adjustmentInfoList = ArrayList<AdjustmentInfo>()

    private var adapter: AdjustmentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            infoId = it.getString(ARG_INFO_ID)?:""
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_adjustment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        adapter = AdjustmentAdapter(adjustmentInfoList, LayoutInflater.from(context)) { info, newValue ->
            onInfoChange(info, newValue)
        }
        recyclerView.adapter = adapter
        notifyDataSetChanged()
    }

    protected fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    protected fun addAdjustmentInfo(vararg infos: AdjustmentInfo) {
        adjustmentInfoList.clear()
        adjustmentInfoList.addAll(infos)
        notifyDataSetChanged()
    }

    protected fun notifyInfoChange(newInfo: AdjustmentInfo, ifAdd: Boolean = true) {
        var index = adjustmentInfoList.indexOf(newInfo)
        if (index < 0) {
            for (i in 0 until adjustmentInfoList.size) {
                val info = adjustmentInfoList[i]
                if (info.key == newInfo.key) {
                    info.copy(newInfo)
                    index = i
                    break
                }
            }
        }
        if (ifAdd && index < 0) {
            adjustmentInfoList.add(newInfo)
            index = adjustmentInfoList.size - 1
        }
        if (index >= 0 && index < adjustmentInfoList.size) {
            adapter?.notifyItemChanged(index)
        }
    }

    abstract fun onInfoChange(info: AdjustmentInfo, newValue: Any)

    /**
     * 向外部提供Panel的View
     */
    abstract fun getPanelView(): View

    /**
     * 返回当前调整的结果
     */
    abstract fun getPanelInfo(): PanelInfo

    /**
     * 开始加载
     */
    protected fun startLoading() {
        infoLoadCallback?.onInfoLoadStatusChange(true)
    }

    /**
     * 停止加载
     */
    protected fun stopLoading() {
        infoLoadCallback?.onInfoLoadStatusChange(false)
    }

    protected abstract fun onInfoFoundById(info: PanelInfo?)

    /**
     * 当面板初始化完成时，调用方法出发完成事件
     */
    protected fun panelInitComplete() {
        infoLoadCallback?.onPanelInitComplete()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (infoId.isNotEmpty()) {
            startLoading()
            doAsync ({ e ->
                logE("init panel info from database error:" + e.localizedMessage)
            }) {
                DatabaseHelper.read(activity!!).findInfoById(infoId) { resultInfo ->
                    uiThread {
                        stopLoading()
                        onInfoFoundById(resultInfo)
                    }
                }.close()
            }
        } else {
            onInfoFoundById(null)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is InfoLoadCallback) {
            infoLoadCallback = context
        } else {
            throw RuntimeException("context is not interface InfoLoadCallback")
        }
    }

    interface InfoLoadCallback {
        fun onInfoLoadStatusChange(isLoading: Boolean)
        fun onPanelInitComplete()
        fun getSizeChangeCallback(): PanelSizeChangeCallback
    }

    interface PanelSizeChangeCallback {
        fun setPanelSize(spanX: Int, spanY: Int)
    }

    fun switch(run: AdjustmentBoolean.() -> Unit): AdjustmentBoolean {
        return AdjustmentBoolean(run)
    }

    fun seekBar(run: AdjustmentInteger.() -> Unit): AdjustmentInteger {
        return AdjustmentInteger(run)
    }

    fun colors(run: AdjustmentColor.() -> Unit): AdjustmentColor {
        return AdjustmentColor(run)
    }

}