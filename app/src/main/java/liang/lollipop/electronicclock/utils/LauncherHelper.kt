package liang.lollipop.electronicclock.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.Drawable
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.activity.PrivateAppsSettingActivity


/**
 * @author lollipop
 * @date 2019-11-24 14:19
 * 启动器的逻辑以及数据辅助类
 */
class LauncherHelper {

    companion object {
        private const val KEY_PRIVATE_PACKAGES = "KEY_PRIVATE_PACKAGES"
        private const val DELIMITERS = ","

        private var isPackageChange = true

        private val publicAppInfoList: ArrayList<AppInfo> = ArrayList()
        private val privateAppInfoList: ArrayList<AppInfo> = ArrayList()

        fun getAppList(context: Context, publicInfoList: ArrayList<AppInfo>,
                       privateInfoList: ArrayList<AppInfo>): Boolean {
            val isChanged = isPackageChange
            if (isPackageChange) {
                isPackageChange = false
                // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
                val resolveIntent = Intent(Intent.ACTION_MAIN, null)
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
                val packageManager = context.packageManager
                val resolveInfoList = packageManager.queryIntentActivities(resolveIntent, 0)
                // 获取隐藏的包列表
                val privatePkgs = privatePackages(context)

                publicAppInfoList.clear()
                privateAppInfoList.clear()
                for (info in resolveInfoList) {
                    val packageName = info.activityInfo.packageName
                    val intent = getIntent(context, packageName)?:continue
                    val appInfo = AppInfo(
                        info.activityInfo.loadLabel(packageManager).toString(),
                        intent,
                        info.loadIcon(packageManager),
                        packageName
                    )
                    if (privatePkgs.contains(packageName)) {
                        privateAppInfoList.add(appInfo)
                    } else {
                        publicAppInfoList.add(appInfo)
                    }
                }
            }

            publicInfoList.clear()
            publicInfoList.addAll(publicAppInfoList)
            privateInfoList.clear()
            privateInfoList.addAll(privateAppInfoList)

            return isChanged
        }

        private fun privatePackages(context: Context): Array<String>{
            val value = context.getPreferences(KEY_PRIVATE_PACKAGES, "")
            if (value.isEmpty()) {
                return arrayOf()
            }
            val packages = value.split(DELIMITERS)
            return Array(packages.size) { i -> packages[i] }
        }

        fun setPrivatePackages(context: Context, array: Array<String>) {
            isPackageChange = true
            val builder = StringBuilder()
            for (index in array.indices) {
                if (index != 0) {
                    builder.append(DELIMITERS)
                }
                builder.append(array[index])
            }
            context.putPreferences(KEY_PRIVATE_PACKAGES, builder.toString())
        }

        fun launcherTo(context: Context, appInfo: AppInfo) {
            context.startActivity(appInfo.intent)
        }

        private fun getIntent(context: Context, packageName: String): Intent? {
            return context.packageManager.getLaunchIntentForPackage(packageName)
        }

        fun getSettingBtn(context: Context): AppInfo {
            return AppInfo(context.resources.getString(R.string.add_private),
                Intent(context, PrivateAppsSettingActivity::class.java),
                TintUtil.tintDrawable(context, R.drawable.ic_add_black_24dp)
                    .setColor(Color.WHITE).mutate().tint(), "")
        }

        private val packageChangeListenerList = ArrayList<PackageChangeListener>()

        fun addPackageChangeListener(lis: PackageChangeListener) {
            packageChangeListenerList.add(lis)
        }

        fun removePackageChangeListener(lis: PackageChangeListener) {
            packageChangeListenerList.add(lis)
        }

    }

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isPackageChange = true
            packageChangeListenerList.forEach {
                it.onPackageChanged()
            }
        }
    }

    private val packageReceiverFilter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_CHANGED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addDataScheme("package")
    }

    fun registerReceiver(context: Context) {
        context.registerReceiver(packageReceiver, packageReceiverFilter)
    }

    fun unregisterReceiver(context: Context) {
        context.unregisterReceiver(packageReceiver)
    }

    interface PackageChangeListener {
        fun onPackageChanged()
    }

    data class AppInfo(val name: String, val intent: Intent, val icon: Drawable, val packageName: String)

}