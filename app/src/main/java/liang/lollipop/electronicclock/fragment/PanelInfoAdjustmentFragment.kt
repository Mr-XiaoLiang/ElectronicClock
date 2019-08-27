package liang.lollipop.electronicclock.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            infoId = it.getString(ARG_INFO_ID)?:""
        }
    }

    /**
     * 向外部提供Panel的View
     */
    abstract fun getPanelView(): View

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

    protected fun onInfoFoundById(info: PanelInfo?) {

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
    }

}