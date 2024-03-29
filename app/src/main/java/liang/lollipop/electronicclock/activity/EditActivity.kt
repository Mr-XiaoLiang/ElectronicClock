package liang.lollipop.electronicclock.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import liang.lollipop.base.WindowInsetsHelper
import liang.lollipop.base.fixInsetsByPadding
import liang.lollipop.base.lazyBind
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.ActionInfo
import liang.lollipop.electronicclock.bean.WidgetInfo
import liang.lollipop.electronicclock.databinding.ActivityEditBinding
import liang.lollipop.electronicclock.edit.EditAction
import liang.lollipop.electronicclock.list.ActionAdapter
import liang.lollipop.electronicclock.utils.*
import liang.lollipop.guidelinesview.Guidelines
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.panel.SystemWidgetPanel
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.widget.PanelAdapter

/**
 * 编辑用的Activity
 * @author Lollipop
 */
class EditActivity : BaseActivity() {

    companion object {
        private const val SHOW_EDIT_GUIDELINES = "EDIT_ACTIVITY_SHOW_EDIT_GUIDELINES"

        const val ARG_IS_PORTRAIT = "ARG_IS_PORTRAIT"

        fun startByPortrait(activity: Activity) {
            val intent = Intent(activity, EditActivity::class.java)
            intent.putExtra(ARG_IS_PORTRAIT, true)
            activity.startActivity(intent)
        }

        fun startByLandscape(activity: Activity) {
            val intent = Intent(activity, EditActivity::class.java)
            intent.putExtra(ARG_IS_PORTRAIT, false)
            activity.startActivity(intent)
        }

        private const val MIN_LOAD_TIME = 500L
    }

    private val binding: ActivityEditBinding by lazyBind()

    private val logger = Utils.loggerI("EditActivity")

    /**
     * 是否是竖屏
     */
    private var isPortrait = true

    /**
     * 如果是横屏，那么我们认为下方的是action的列表
     * 否则认为右侧是action列表，这样做的目的是为了
     * 使比较窄的那一个列表用来防止action
     */
    private val actionList: RecyclerView
        get() {
            return if (isPortrait) {
                binding.rightList
            } else {
                binding.bottomList
            }
        }

    /**
     * 如果是竖屏，那么我们认为底部的是小部件列表
     * 否则认为右侧是小部件列表，
     * 这样做的目的是始终让比较宽的列表来放置小部件
     */
    private val widgetList: RecyclerView
        get() {
            return if (isPortrait) {
                binding.bottomList
            } else {
                binding.rightList
            }
        }

    private var startLoadingTime = 0L

    private val actionInfoArray = ArrayList<ActionInfo>()

    private val widgetInfoArray = ArrayList<WidgetInfo>()

    private lateinit var widgetHelper: WidgetHelper

    private var isShowGuidelines = true

    private var onInfoChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setScreenOrientation()
        fullScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        isPortrait = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        initView()
        initActions()
        initWidgets()
        initGuidelines()
        initData()
        broadcastHelper.addActions(BroadcastHelper.ACTION_WIDGET_INFO_CHANGE)
        broadcastHelper.register(this)
    }

    private fun initGuidelines() {
        isShowGuidelines = getPreferences(SHOW_EDIT_GUIDELINES, true)
        if (isShowGuidelines) {
            binding.widgetGroup.onPanelAddedListener {
                if (isShowGuidelines && it.view != null) {
                    isShowGuidelines = false
                    Guidelines.target(it.view!!).showIn(this).value(R.string.guidelines_panel_item)
                        .onClose {
                            putPreferences(SHOW_EDIT_GUIDELINES, false)
                            binding.widgetGroup.onPanelAddedListener(null)
                        }.show()
                }
            }
        }
    }

    override fun onReceive(action: String, intent: Intent) {
        super.onReceive(action, intent)
        when (action) {
            BroadcastHelper.ACTION_WIDGET_INFO_CHANGE -> {
                onInfoChange = true
            }
            else -> {}
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        // 退出预览模式的按钮
        binding.exitPreviewBtn.setOnClickListener {
            isPreview(false)
        }

        // 绘制格子在屏幕上，以便观察屏幕
        binding.widgetGroup.drawGrid = true
        binding.widgetGroup.gridColor = Color.GRAY

        binding.widgetGroup.gridCount = this.gridSize
        // 通过辅助类来完成标准小部件容器的事件绑定
        widgetHelper = PreferenceHelper.createWidgetHelper(this, binding.widgetGroup).let {
            it.isAutoInverted = false
            it.canDrag = true
            it.isInEditMode = true
            it
        }.onCantLayout {
            // 当出现无法排版的面板时
            Toast.makeText(this, "出现了${it.size}个无法排版的View", Toast.LENGTH_SHORT).show()
            for (panel in it) {
                widgetHelper.removePanel(panel)
            }
        }.onSelectWidgetError {
            // 当选择系统小部件时，出现异常的提示
            Toast.makeText(this, "选择系统小部件时出现异常", Toast.LENGTH_SHORT).show()
        }


        binding.loadView.setOnTouchListener { _, _ -> true }

        binding.rootGroup.fixInsetsByPadding(WindowInsetsHelper.Edge.ALL)
    }

    /**
     * 初始化控制按钮的列表
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun initActions() {
        actionInfoArray.add(
            ActionInfo(
                EditAction.BACK,
                R.drawable.ic_arrow_back_black_24dp,
                R.string.action_back
            ),
            ActionInfo(
                EditAction.DONE,
                R.drawable.ic_done_black_24dp,
                R.string.action_done
            ),
            ActionInfo(
                EditAction.ADJUSTMENT,
                R.drawable.ic_settings_black_24dp,
                R.string.action_adjustment
            ),
            ActionInfo(
                EditAction.DELETE,
                R.drawable.ic_delete_black_24dp,
                R.string.action_delete
            ),
            ActionInfo(
                EditAction.WIDGET,
                R.drawable.ic_dashboard_black_24dp,
                R.string.action_widget
            ),
            ActionInfo(
                EditAction.PREVIEW,
                R.drawable.ic_visibility_black_24dp,
                R.string.action_preview
            ),
            ActionInfo(
                EditAction.INVERTED,
                R.drawable.ic_invert_colors_black_24dp,
                R.string.action_inverted
            ),
            ActionInfo(
                EditAction.AUTO_LIGHT,
                R.drawable.ic_brightness_auto_black_24dp,
                R.string.action_auto_light
            ),
            ActionInfo(
                EditAction.RESET,
                R.drawable.ic_replay_black_24dp,
                R.string.action_reset
            )
        )

        val adapter = ActionAdapter(actionInfoArray, layoutInflater, true) { holder ->
            onActionSelected(actionInfoArray[holder.adapterPosition].action)
        }
        val orientation = if (isPortrait) {
            RecyclerView.VERTICAL
        } else {
            RecyclerView.HORIZONTAL
        }
        actionList.layoutManager = LinearLayoutManager(this, orientation, false)
        actionList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initWidgets() {
        widgetInfoArray.addAll(LPanelProviders.getWidgetInfoList())

        val adapter = ActionAdapter(widgetInfoArray, layoutInflater, false) { holder ->
            onWidgetSelected(widgetInfoArray[holder.adapterPosition])
        }
        val orientation = if (isPortrait) {
            RecyclerView.HORIZONTAL
        } else {
            RecyclerView.VERTICAL
        }
        widgetList.layoutManager = StaggeredGridLayoutManager(2, orientation)
        widgetList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initData() {
        widgetHelper.updateByDB { status ->
            if (status == WidgetHelper.LoadStatus.START) {
                startLoading()
            } else {
                stopLoading()
                if (isShowGuidelines) {
                    widgetHelper.addPanel(ClockPanelInfo())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        widgetHelper.onStart()
    }

    override fun onStop() {
        super.onStop()
        widgetHelper.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        if (onInfoChange) {
            onInfoChange = false
            alert {
                // 设置对话框内容
                setMessage(R.string.dialog_message_on_info_change)
                setPositiveButton(R.string.enter) { dialog, _ ->
                    initData()
                    dialog.dismiss()
                }
                // 取消按钮，使用语义明确的文字来作为对话框的按钮文字
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    override fun onDestroy() {
        broadcastHelper.unregister(this)
        super.onDestroy()
    }

    /**
     * 当操作按钮被点击时的事件处理方法
     * @param action 时间的id
     */
    private fun onActionSelected(action: EditAction) {
        when (action) {
            // 返回
            EditAction.BACK -> {
                onBackPressed()
            }
            // 删除当前选中的小部件
            EditAction.DELETE -> {
                widgetHelper.removeSelectedPanel()
            }
            // 完成并保存
            EditAction.DONE -> {
                widgetHelper.saveToDB {
                    if (it == WidgetHelper.LoadStatus.START) {
                        startLoading()
                    } else {
                        stopLoading()
                        Snackbar.make(
                            binding.widgetGroup,
                            R.string.saved,
                            Snackbar.LENGTH_LONG
                        ).show()
                        initData()
                    }
                }
            }
            // 选择并添加系统小部件
            EditAction.WIDGET -> {
                widgetHelper.selectAppWidget()
            }
            // 启动预览模式
            EditAction.PREVIEW -> {
                isPreview(true)
            }
            // 反色调整
            EditAction.INVERTED -> {
                widgetHelper.isInverted = !widgetHelper.isInverted
            }
            // 自动亮度
            EditAction.AUTO_LIGHT -> {
                widgetHelper.isAutoLight = !widgetHelper.isAutoLight
            }
            // 重置数据
            EditAction.RESET -> {
                alert {
                    // 设置对话框内容
                    setMessage(R.string.dialog_message_reset)
                    // 设置确认按钮, 使用语义明确的文字来作为对话框的按钮文字
                    // 避免使用让人不确定的文字如：确定
                    setPositiveButton(R.string.dialog_positive_reset) { dialog, _ ->
                        initData()
                        dialog.dismiss()
                    }
                    // 取消按钮，使用语义明确的文字来作为对话框的按钮文字
                    setNegativeButton(R.string.dialog_negative_reset) { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
            // 调整小部件
            EditAction.ADJUSTMENT -> {
                val selectedPanel = binding.widgetGroup.selectedPanel
                if (selectedPanel == null) {
                    Snackbar.make(
                        binding.widgetGroup,
                        R.string.must_place_widget,
                        Snackbar.LENGTH_LONG
                    ).show()
                    return
                }
                if (selectedPanel is SystemWidgetPanel) {
                    selectedPanel.callWidgetClick()
                    return
                }
                val intent = selectedPanel.panelInfo.getIntent()
                if (intent == null) {
                    Snackbar.make(
                        binding.widgetGroup,
                        R.string.no_adjusted_panels,
                        Snackbar.LENGTH_LONG
                    ).show()
                    return
                }
                startActivity(intent)
            }
        }
    }

    /**
     * 当小部件列表被点击的时候，用于处理事件的方法
     * 一般情况为添加一个小部件到屏幕
     */
    private fun onWidgetSelected(info: WidgetInfo) {
        widgetHelper.addPanel(PanelAdapter.newInfo(info.infoName))
    }

    private fun isPreview(value: Boolean) {
        val groupAnimator = binding.widgetGroup.animate()
        groupAnimator.cancel()

        val rightAnimator = binding.rightList.animate()
        rightAnimator.cancel()

        val bottomAnimator = binding.bottomList.animate()
        bottomAnimator.cancel()
        if (value) {
            binding.exitPreviewBtn.visibility = View.VISIBLE
            logger("widgetGroup-mini:[${binding.widgetGroup.width}, ${binding.widgetGroup.height}]")
            groupAnimator
                .scaleX(1F)
                .scaleY(1F)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        groupAnimator.setListener(null)
                        binding.widgetGroup.drawGrid = false
                        logger("widgetGroup-expand:[${binding.widgetGroup.width}, ${binding.widgetGroup.height}]")
                    }
                }).start()
            rightAnimator.translationX(binding.rightList.width.toFloat()).start()
            bottomAnimator.translationY(binding.bottomList.height.toFloat()).start()
        } else {
            binding.widgetGroup.drawGrid = true
            binding.exitPreviewBtn.visibility = View.INVISIBLE
            groupAnimator.scaleX(0.8F).scaleY(0.8F).start()
            rightAnimator.translationX(0F).start()
            bottomAnimator.translationY(0F).start()
        }
        binding.widgetGroup.lockedTouch = value
        binding.widgetGroup.selectedPanel = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (widgetHelper.onActivityResult(requestCode, resultCode, data)) {
            return
        }
    }

    private fun setScreenOrientation() {
        val o = intent.getBooleanExtra(ARG_IS_PORTRAIT, true)
        requestedOrientation = if (o) {
            //竖屏
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    private fun startLoading() {
        startLoadingTime = System.currentTimeMillis()
        binding.loadView.alpha = 0F
        val animate = binding.loadView.animate()
        animate.cancel()
        animate.alpha(1F)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    animate.setListener(null)
                    binding.loadView.visibility = View.VISIBLE
                    binding.loadProgressBar.isIndeterminate = true
                }
            }).start()
    }

    private fun stopLoading() {
        val diff = MIN_LOAD_TIME + startLoadingTime - System.currentTimeMillis()
        if (diff > 0) {
            binding.loadView.postDelayed({ stopLoading() }, diff)
            return
        }
        val animate = binding.loadView.animate()
        animate.cancel()
        animate.alpha(0F)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    animate.setListener(null)
                    binding.loadView.visibility = View.INVISIBLE
                    binding.loadProgressBar.isIndeterminate = false
                }
            }).start()
    }

    private fun <T> ArrayList<T>.add(vararg values: T) {
        this.addAll(values)
    }

}
