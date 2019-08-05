package liang.lollipop.widget.utils

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.UserManager


/**
 * @author lollipop
 * @date 2019-08-05 13:46
 * 系统应用的小部件的辅助类
 */
class AppWidgetHelper {

    fun getAllWidgetProviderInfo(context: Context): ArrayList<AppWidgetProviderInfo> {
        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        val providers = ArrayList<AppWidgetProviderInfo>()
        for (user in userManager.userProfiles) {
            providers.addAll(widgetManager.getInstalledProvidersForProfile(user))
        }
        return providers
    }

}