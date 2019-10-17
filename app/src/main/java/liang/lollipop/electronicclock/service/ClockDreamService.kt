package liang.lollipop.electronicclock.service

import android.content.res.Configuration
import android.service.dreams.DreamService
import android.view.View
import android.view.WindowInsets
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.PreferenceHelper
import liang.lollipop.electronicclock.view.ContentLoadingProgressBar
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.widget.WidgetGroup

/**
 * @author lollipop
 * @date 2019-10-15 23:54
 * 时钟的屏保服务
 *
 */
class ClockDreamService: DreamService() {

    private var widgetHelper: WidgetHelper? = null

    private val widgetGroup: WidgetGroup?
        get() {
            return findViewById(R.id.widgetGroup)
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Exit dream upon user touch
        isInteractive = false
        // Hide system UI
        isFullscreen = true
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setContentView(R.layout.dream_widget)
        initView()
        initData()
    }

    private fun initView() {
        widgetGroup?.let { group ->
            insetListener(group) { insets ->
                val left = insets.systemWindowInsetLeft
                val top = insets.systemWindowInsetTop
                val right = insets.systemWindowInsetRight
                val bottom = insets.systemWindowInsetBottom
                group.setPadding(left, top, right, bottom)
            }
            widgetHelper = PreferenceHelper.createWidgetHelper(this, group)
        }

    }

    private fun initData() {
        val loadingView = findViewById<ContentLoadingProgressBar>(R.id.loadingView)
        loadingView.putColorForRes(R.color.colorAccent, R.color.colorPrimary)
        widgetHelper?.updateByDB { status ->
            if (status == WidgetHelper.LoadStatus.START) {
                loadingView.show()
            } else {
                loadingView.hide()
                if (widgetHelper?.panelCount?:0 < 1) {
                    onWidgetEmpty()
                }
            }
        }
    }

    private fun onWidgetEmpty() {
        widgetGroup?.post {
            widgetGroup?.let {
                val clockPanelInfo = ClockPanelInfo()
                clockPanelInfo.sizeChange(it.spanCountX, 1)
                clockPanelInfo.offset(0, 0)
                widgetHelper?.addPanel(clockPanelInfo)
            }
        }
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        widgetHelper?.onStart()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        widgetHelper?.onStop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        widgetHelper = null
    }

    private fun insetListener(group: View, onInsetChange: (insets: WindowInsets) -> Unit) {
        group.fitsSystemWindows = true
        group.setOnApplyWindowInsetsListener { _, insets ->
            onInsetChange(insets)
            insets.consumeStableInsets()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        widgetHelper?.postOnConfigurationChanged(newConfig, {
            initData()
        })
    }
}