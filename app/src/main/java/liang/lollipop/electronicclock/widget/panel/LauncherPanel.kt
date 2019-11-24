package liang.lollipop.electronicclock.widget.panel

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.utils.LauncherHelper
import liang.lollipop.electronicclock.utils.TintUtil
import liang.lollipop.electronicclock.widget.info.LauncherPanelInfo
import liang.lollipop.widget.utils.FloatingViewHelper
import liang.lollipop.widget.utils.doAsync
import liang.lollipop.widget.utils.uiThread
import liang.lollipop.widget.widget.Panel

/**
 * @author lollipop
 * @date 2019-11-22 21:50
 * 启动器面板
 */
class LauncherPanel(info: LauncherPanelInfo): Panel<LauncherPanelInfo>(info) {

    private var iconViewId = 0

    override fun createView(context: Context): View? {
        val cardView = MaterialCardView(context)

        val iconView = ImageView(context)
        iconView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        iconViewId = View.generateViewId()
        iconView.id = iconViewId

        TintUtil.tintDrawable(context,
            R.drawable.ic_apps_white_24dp)
            .setColor(panelInfo.iconColor)
            .into(iconView)

        cardView.addView(iconView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        cardView.radius = panelInfo.radius
        cardView.cardElevation = panelInfo.elevation
        cardView.setCardBackgroundColor(panelInfo.btnColor)

        if (!isInEditMode) {
            AppPanel(cardView)
        }
        return cardView
    }

    class AppPanel(anchorView: View) {
        private val floatingViewHelper = FloatingViewHelper.create(anchorView, R.layout.fragment_launcher)
        private val recyclerView: RecyclerView

        private val appList = ArrayList<LauncherHelper.AppInfo>()
        private val privateAppList = ArrayList<LauncherHelper.AppInfo>()
        private val publicAppList = ArrayList<LauncherHelper.AppInfo>()
        private val context = anchorView.context

        private var isPrivate = false

        private val itemWidth = context.resources.getDimensionPixelSize(R.dimen.launcher_item_width)

        init {
            recyclerView = floatingViewHelper.findFromContent(R.id.recyclerView)

            val bottomSheetBehavior = BottomSheetBehavior.from(
                floatingViewHelper.findFromContent<View>(R.id.sheetGroup))

            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        floatingViewHelper.close()
                    }
                }
            })
            anchorView.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                onAnchorViewClick(false)
            }
            anchorView.setOnLongClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                onAnchorViewClick(true)
                true
            }

            val adapter = AppAdapter(appList) {
                LauncherHelper.launcherTo(context, appList[it])
                floatingViewHelper.close()
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = StaggeredGridLayoutManager(4, RecyclerView.VERTICAL)

            floatingViewHelper.onShow {
                onPanelShown()
            }
        }

        private fun onAnchorViewClick(isLongClick: Boolean) {
            isPrivate = isLongClick
            floatingViewHelper.findFromContent<View>(R.id.privateIconView)?.let {
                it.visibility = if (isPrivate) { View.VISIBLE } else { View.INVISIBLE }
            }
            floatingViewHelper.showOutermost(FloatingViewHelper.AnimationType.Bottom)
        }

        private fun onPanelShown() {
            recyclerView.layoutManager?.let { manager ->
                if (manager is StaggeredGridLayoutManager) {
                    manager.spanCount = recyclerView.width / itemWidth
                }
            }
            doAsync {
                LauncherHelper.getAppList(context, publicAppList, privateAppList)
                uiThread {
                    appList.clear()
                    if (isPrivate) {
                        appList.addAll(privateAppList)
                    } else {
                        appList.addAll(publicAppList)
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }

    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        if (panelInfo.isAutoColor) {
            tryMyView<MaterialCardView> {
                it.setCardBackgroundColor(color)
                it.findViewById<ImageView>(iconViewId)?.let { iconView ->
                    val iconColor = if (color and 0xFFFFFF == 0) {
                        // 说明卡片是黑色，那么icon取相反的颜色
                        val alpha = (255 * light).toInt()
                        Color.WHITE and 0xFFFFFF or (alpha shl 24)
                    } else {
                        Color.BLACK
                    }
                    TintUtil.tintDrawable(iconView.drawable)
                        .setColor(iconColor)
                        .into(iconView)
                }
            }
        }
    }

    override fun onInfoChange() {
        super.onInfoChange()
        tryMyView<MaterialCardView> { cardView ->
            cardView.radius = panelInfo.radius
            cardView.cardElevation = panelInfo.elevation
            if (!panelInfo.isAutoColor) {
                cardView.setCardBackgroundColor(panelInfo.btnColor)
                cardView.findViewById<ImageView>(iconViewId)?.let { iconView ->
                    TintUtil.tintDrawable(iconView.drawable)
                        .setColor(panelInfo.iconColor)
                        .into(iconView)
                }
            }
        }
    }

    private class AppAdapter(
        private val data: ArrayList<LauncherHelper.AppInfo>,
        private val onClickListener: (Int) -> Unit): RecyclerView.Adapter<AppHolder>() {

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
            holder.onBind(data[position])
        }

    }

    private class AppHolder private constructor(view: View,
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

        init {
            view.setOnClickListener {
                onClickListener(this)
            }
        }

        fun onBind(appInfo: LauncherHelper.AppInfo) {
            iconView.setImageDrawable(appInfo.icon)
            nameView.text = appInfo.name
        }

    }

}