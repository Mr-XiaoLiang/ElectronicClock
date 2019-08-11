package liang.lollipop.electronicclock.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_edit.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.list.ActionAdapter
import liang.lollipop.electronicclock.list.ActionInfo
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.utils.dp

/**
 * 编辑用的Activity
 * @author Lollipop
 */
class EditActivity : BaseActivity() {

    companion object {
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

        private const val ACTION_DELETE = -1
        private const val ACTION_DONE = 0
        private const val ACTION_BACK = 1
        private const val ACTION_WIDGET = 2
        private const val ACTION_PREVIEW = 3
        private const val ACTION_INVERTED = 4
        private const val ACTION_AUTO_LIGHT = 5
        private const val ACTION_RESET = 6

        private const val MIN_LOAD_TIME = 800L
    }

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
            return if (isPortrait) { rightList } else { bottomList }
        }

    /**
     * 如果是竖屏，那么我们认为底部的是小部件列表
     * 否则认为右侧是小部件列表，
     * 这样做的目的是始终让比较宽的列表来放置小部件
     */
    private val widgetList: RecyclerView
        get() {
            return if (isPortrait) { bottomList } else { rightList }
        }

    private var startLoadingTime = 0L

    private val actionInfoArray = ArrayList<ActionInfo>()

    private lateinit var widgetHelper: WidgetHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setScreenOrientation()
        fullScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        isPortrait = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        initInsetListener(rootGroup)
        initView()
        initActions()

        initData()
    }

    private fun initView() {
        // 始终让右侧的列表上下滚动
        rightList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        // 始终让底部的列表左右滚动
        bottomList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // 退出预览模式的按钮
        exitPreviewBtn.setOnClickListener {
            isPreview(false)
        }

        // 绘制格子在屏幕上，以便观察屏幕
        widgetGroup.drawGrid = true
        widgetGroup.gridColor = Color.GRAY
        widgetGroup.scaleX

        // 通过辅助类来完成标准小部件容器的事件绑定
        widgetHelper = WidgetHelper.with(this, widgetGroup).let {
            it.dragStrokeWidth = resources.dp(16F)
            it.selectedBorderWidth = resources.dp(2F)
            it.touchPointRadius = resources.dp(5F)
            it.selectedColor = ContextCompat.getColor(this, R.color.colorPrimary)
            it.focusColor = ContextCompat.getColor(this, R.color.colorAccent)
            it.pendingLayoutTime = 800L
            it.isAutoInverted = false
            it.isPortrait = isPortrait
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


        loadView.setOnTouchListener { _, _ -> true }
    }

    /**
     * 初始化控制按钮的列表
     */
    private fun initActions() {
        actionInfoArray.add(ActionInfo(ACTION_BACK, R.drawable.ic_arrow_back_black_24dp, R.string.action_back))
        actionInfoArray.add(ActionInfo(ACTION_DONE, R.drawable.ic_done_black_24dp, R.string.action_done))
        actionInfoArray.add(ActionInfo(ACTION_DELETE, R.drawable.ic_delete_black_24dp, R.string.action_delete))
        actionInfoArray.add(ActionInfo(ACTION_WIDGET, R.drawable.ic_dashboard_black_24dp, R.string.action_widget))
        actionInfoArray.add(ActionInfo(ACTION_PREVIEW, R.drawable.ic_visibility_black_24dp, R.string.action_preview))
        actionInfoArray.add(ActionInfo(ACTION_INVERTED, R.drawable.ic_invert_colors_black_24dp, R.string.action_inverted))
        actionInfoArray.add(ActionInfo(ACTION_AUTO_LIGHT, R.drawable.ic_brightness_auto_black_24dp, R.string.action_auto_light))
        actionInfoArray.add(ActionInfo(ACTION_RESET, R.drawable.ic_replay_black_24dp, R.string.action_reset))

        val adapter = ActionAdapter(actionInfoArray, layoutInflater) { holder ->
            onActionSelected(actionInfoArray[holder.adapterPosition].action)
        }
        actionList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initData() {
        widgetHelper.updateByDB { status ->
            if (status == WidgetHelper.LoadStatus.START) {
                startLoading()
            } else {
                stopLoading()
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

    /**
     * 当操作按钮被点击时的事件处理方法
     * @param action 时间的id
     */
    private fun onActionSelected(action: Int) {
        when(action) {
            // 返回
            ACTION_BACK -> {
                onBackPressed()
            }
            // 删除当前选中的小部件
            ACTION_DELETE -> {
                widgetHelper.removeSelectedPanel()
            }
            // 完成并保存
            ACTION_DONE -> {
                widgetHelper.saveToDB { status ->
                    if (status == WidgetHelper.LoadStatus.START) {
                        startLoading()
                    } else {
                        stopLoading()
                    }
                }
            }
            // 选择并添加系统小部件
            ACTION_WIDGET -> {
                widgetHelper.selectAppWidget()
            }
            // 启动预览模式
            ACTION_PREVIEW -> {
                isPreview(true)
            }
            // 反色调整
            ACTION_INVERTED -> {
                widgetHelper.isInverted = !widgetHelper.isInverted
            }
            // 自动亮度
            ACTION_AUTO_LIGHT -> {
                widgetHelper.isAutoLight = !widgetHelper.isAutoLight
            }
            // 重置数据
            ACTION_RESET -> {
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
        }
    }

    private fun isPreview(value: Boolean) {
        val groupAnimator = widgetGroup.animate()
        groupAnimator.cancel()

        val rightAnimator = rightList.animate()
        rightAnimator.cancel()

        val bottomAnimator = bottomList.animate()
        bottomAnimator.cancel()
        if (value) {
            exitPreviewBtn.visibility = View.VISIBLE
            groupAnimator
                .scaleX(1F)
                .scaleY(1F)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        groupAnimator.setListener(null)
                        widgetGroup.drawGrid = false
                    }
                }).start()
            rightAnimator.translationX(rightList.width.toFloat()).start()
            bottomAnimator.translationY(bottomList.height.toFloat()).start()
        } else {
            widgetGroup.drawGrid = true
            exitPreviewBtn.visibility = View.INVISIBLE
            groupAnimator.scaleX(0.8F).scaleY(0.8F).start()
            rightAnimator.translationX(0F).start()
            bottomAnimator.translationY(0F).start()
        }
        widgetGroup.lockedTouch = value
        widgetGroup.selectedPanel = null
    }

    override fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        super.onWindowInsetsChange(left, top, right, bottom)
        rootGroup.setPadding(left, top, right, bottom)
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
        loadView.alpha = 0F
        val animate = loadView.animate()
        animate.cancel()
        animate.alpha(1F)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    animate.setListener(null)
                    loadView.visibility = View.VISIBLE
                    loadProgressBar.isIndeterminate = true
                }
            }).start()
    }

    private fun stopLoading() {
        val diff = MIN_LOAD_TIME + startLoadingTime - System.currentTimeMillis()
        if (diff > 0) {
            loadView.postDelayed({ stopLoading() }, diff)
            return
        }
        val animate = loadView.animate()
        animate.cancel()
        animate.alpha(0F)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    animate.setListener(null)
                    loadView.visibility = View.INVISIBLE
                    loadProgressBar.isIndeterminate = false
                }
            }).start()
    }

}
