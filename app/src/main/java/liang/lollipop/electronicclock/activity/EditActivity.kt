package liang.lollipop.electronicclock.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
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
    }

    private fun initView() {
        // 始终让右侧的列表上下滚动
        rightList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        // 始终让底部的列表左右滚动
        bottomList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        widgetGroup.drawGrid = true
        widgetGroup.gridColor = Color.GRAY
        widgetGroup.scaleX

        widgetHelper = WidgetHelper.with(this, widgetGroup).let {
            it.dragStrokeWidth = resources.dp(16F)
            it.selectedBorderWidth = resources.dp(2F)
            it.touchPointRadius = resources.dp(5F)
            it.selectedColor = ContextCompat.getColor(this, R.color.colorPrimary)
            it.focusColor = ContextCompat.getColor(this, R.color.colorAccent)
            it.pendingLayoutTime = 800L
            it
        }.onCantLayout {
            Toast.makeText(this, "出现了${it.size}个无法排版的View", Toast.LENGTH_SHORT).show()
            for (panel in it) {
                widgetHelper.removePanel(panel)
            }
        }

        widgetHelper.onSelectWidgetError {
            Toast.makeText(this, "选择系统小部件时出现异常", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initActions() {
        actionInfoArray.add(ActionInfo(ACTION_BACK, R.drawable.ic_arrow_back_black_24dp, R.string.action_back))
        actionInfoArray.add(ActionInfo(ACTION_DELETE, R.drawable.ic_delete_black_24dp, R.string.action_delete))
        actionInfoArray.add(ActionInfo(ACTION_DONE, R.drawable.ic_done_black_24dp, R.string.action_done))
        actionInfoArray.add(ActionInfo(ACTION_WIDGET, R.drawable.ic_dashboard_black_24dp, R.string.action_widget))

        val adapter = ActionAdapter(actionInfoArray, layoutInflater) { holder ->
            onActionSelected(actionInfoArray[holder.adapterPosition].action)
        }
        actionList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun onActionSelected(action: Int) {
        when(action) {
            ACTION_BACK -> {
                onBackPressed()
            }
            ACTION_DELETE -> {
                widgetHelper.removeSelectedPanel()
            }
            ACTION_DONE -> {

            }
            ACTION_WIDGET -> {
                widgetHelper.selectAppWidget()
            }
        }
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

}
