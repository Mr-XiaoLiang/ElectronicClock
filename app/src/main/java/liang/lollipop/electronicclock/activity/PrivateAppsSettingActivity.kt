package liang.lollipop.electronicclock.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_private_apps_setting.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.LauncherHelper
import liang.lollipop.widget.utils.doAsync
import liang.lollipop.widget.utils.uiThread

/**
 * @author lollipop
 * @date 2019-11-24 18:01
 * 私有App设定的Activity
 */
class PrivateAppsSettingActivity: BottomNavigationActivity() {

    override val contentViewId: Int
        get() = R.layout.activity_private_apps_setting

    private val appList = ArrayList<LauncherHelper.AppInfo>()

    private val selectedList = ArrayList<LauncherHelper.AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFAB(R.drawable.ic_done_black_24dp) { fab ->
            fab.setOnClickListener {
                submit()
            }
        }

        initView()
        initData()
    }

    private fun initView() {
        val adapter = AppAdapter(appList, {
            val info = appList[it]
            // 如果移除失败，说明里面没有，那么把他加上去，否则就完成移除
            if (!selectedList.remove(info)) {
                selectedList.add(info)
            }
        }, { selectedList.contains(it) })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(4, RecyclerView.VERTICAL)

        recyclerView.post {
            recyclerView.layoutManager?.let { manager ->
                if (manager is StaggeredGridLayoutManager) {
                    val itemWidth = resources.getDimensionPixelSize(R.dimen.launcher_item_width)
                    manager.spanCount = recyclerView.width / itemWidth
                }
            }
        }
    }

    private fun initData() {
        startContentLoading()
        doAsync {
            LauncherHelper.getAppList(this, appList, selectedList)
            appList.addAll(0, selectedList)
            uiThread {
                stopContentLoading()
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun submit() {
        LauncherHelper.setPrivatePackages(this,
            Array(selectedList.size) { i ->
                selectedList[i].packageName
            })
        onBackPressed()
    }

    private class AppAdapter(
        private val data: ArrayList<LauncherHelper.AppInfo>,
        private val onClickListener: (Int) -> Unit,
        private val selectedProvider: (LauncherHelper.AppInfo) -> Boolean): RecyclerView.Adapter<AppHolder>() {

        private val holderClickListener = { holder: AppHolder ->
            onClickListener(holder.adapterPosition)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppHolder {
            return AppHolder.create(parent, holderClickListener)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: AppHolder, position: Int) {
            val info = data[position]
            holder.onBind(info, selectedProvider(info))
        }

    }

    private class AppHolder private constructor(
        view: View,
        private val onClickListener: (AppHolder) -> Unit): RecyclerView.ViewHolder(view) {
        companion object {
            fun create(group: ViewGroup, onClickListener: (AppHolder) -> Unit): AppHolder {
                return AppHolder(
                    LayoutInflater.from(group.context)
                        .inflate(R.layout.item_launcher_app, group, false),
                    onClickListener)
            }
        }

        private val iconView: ImageView = view.findViewById(R.id.iconView)
        private val nameView: TextView = view.findViewById(R.id.nameView)

        private var isSelected = false

        init {
            view.setOnClickListener {
                onClickListener(this)
                isSelected = !isSelected
                changeBackground()
            }
        }

        fun onBind(appInfo: LauncherHelper.AppInfo, isSelected: Boolean) {
            iconView.setImageDrawable(appInfo.icon)
            nameView.text = appInfo.name
            this.isSelected = isSelected
            changeBackground()
        }

        private fun changeBackground() {
            itemView.setBackgroundResource(if (isSelected) {
                R.drawable.bg_action_widget_btn
            } else  {
                0
            })
        }

    }

}