package liang.lollipop.widget.utils

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.Intent
import liang.lollipop.widget.info.SystemWidgetPanelInfo


/**
 * @author lollipop
 * @date 2019-08-05 13:46
 * 系统应用的小部件的辅助类
 */
class AppWidgetHelper(private val activity: Activity, hostId: Int = DEF_HOST_ID) {

    companion object {
        private const val DEF_HOST_ID = 104096

        private const val REQUEST_SELECT_WIDGET = 996
        private const val REQUEST_CREATE_WIDGET = 995
    }

    private val context = activity.applicationContext

    private val appWidgetHost = AppWidgetHost(context, hostId)

    private val appWidgetManager = AppWidgetManager.getInstance(context)

    private var onSelectWidgetErrorListener: (() -> Unit)? = null

    private var onWidgetCreateListener: ((info: SystemWidgetPanelInfo) -> Unit)? = null

    fun onStart() {
        appWidgetHost.startListening()
    }

    fun onStop() {
        appWidgetHost.stopListening()
    }

    fun selectAppWidget() {
        val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
        val newId = appWidgetHost.allocateAppWidgetId()
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newId)
        activity.startActivityForResult(pickIntent, REQUEST_SELECT_WIDGET)
    }

    fun onSelectWidgetError(lis: () -> Unit) {
        onSelectWidgetErrorListener = lis
    }

    fun onWidgetCreate(lis: (info: SystemWidgetPanelInfo) -> Unit) {
        onWidgetCreateListener = lis
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return when (requestCode) {
            REQUEST_SELECT_WIDGET -> {
                onWidgetSelected(resultCode, data)
                true
            }
            REQUEST_CREATE_WIDGET -> {
                onWidgetCreated(resultCode, data)
                true
            }
            else -> {
                false
            }
        }
    }

    private fun onWidgetSelected(resultCode: Int, data: Intent?) {
        // 如果没有有效的返回值，那么放弃本次结果
        data?:return
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        // 获取小部件的ID，如果获取不到，那么放弃
        val appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return
        }
        // 获取小部件的配置信息
        val appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
        // 如果有有参数配置页面，那么跳转过去
        if (appWidgetProviderInfo.configure != null) {
            val configureIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
            configureIntent.component = appWidgetProviderInfo.configure
            configureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            activity.startActivityForResult(configureIntent, REQUEST_CREATE_WIDGET)
            return
        }
        // 如果没有配置的页面，那么认为直接创建成功了
        onActivityResult(REQUEST_CREATE_WIDGET, Activity.RESULT_OK, data)
    }

    private fun onWidgetCreated(resultCode: Int, data: Intent?) {
        // 如果没有有效的返回值或者没有回调函数，那么放弃本次结果
        data?:return
        if (resultCode != Activity.RESULT_OK || onWidgetCreateListener == null) {
            return
        }
        // 获取有效的信息，否则触发错误
        val appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            onSelectWidgetErrorListener?.invoke()
            return
        }
        // 提取有效数据并传出
        val appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
        val panelInfo = SystemWidgetPanelInfo()
        panelInfo.appWidgetId = appWidgetId
        panelInfo.appWidgetProviderInfo = appWidgetProviderInfo
        onWidgetCreateListener?.invoke(panelInfo)
    }

    fun createViewByInfo(panelInfo: SystemWidgetPanelInfo): AppWidgetHostView? {
        if (panelInfo.isEmpty) {
            return null
        }
        return appWidgetHost.createView(context,
            panelInfo.appWidgetId, panelInfo.appWidgetProviderInfo)
    }

//    fun getAllWidgetProviderInfo(context: Context): ArrayList<AppWidgetProviderInfo> {
//        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
//        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
//        val providers = ArrayList<AppWidgetProviderInfo>()
//        for (user in userManager.userProfiles) {
//            providers.addAll(widgetManager.getInstalledProvidersForProfile(user))
//        }
//        return providers
//    }

}