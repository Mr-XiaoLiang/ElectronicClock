package liang.lollipop.electronicclock.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.PreferenceBoolean
import liang.lollipop.electronicclock.bean.PreferenceChoice
import liang.lollipop.electronicclock.bean.PreferenceInfo
import liang.lollipop.electronicclock.bean.PreferenceNumber
import liang.lollipop.electronicclock.list.PreferenceAdapter
import liang.lollipop.widget.WidgetHelper
import liang.lollipop.widget.utils.dp
import liang.lollipop.widget.widget.WidgetGroup

/**
 * @author lollipop
 * @date 2019-08-16 22:38
 * 偏好设置的辅助类
 */
object PreferenceHelper {

    const val KEY_AUTO_LIGHT = "KEY_AUTO_LIGHT"
    const val KEY_AUTO_INVERTED = "KEY_AUTO_INVERTED"
    const val KEY_INVERTED = "KEY_INVERTED"
    const val KEY_GRID_SIZE = "KEY_GRID_SIZE"
    const val KEY_ORIENTATION = "KEY_ORIENTATION"

    const val ORIENTATION_AUTO = 0
    const val ORIENTATION_PORTRAIT = 1
    const val ORIENTATION_LANDSCAPE = 2

    const val DEF_GRID_SIZE = 6
    private const val MIN_GRID_SIZE = 4
    private const val MAX_GRID_SIZE = 20

    init {
        WidgetHelper.panelProviders(LPanelProviders())
    }

    fun bindPreferenceGroup(group: RecyclerView): PreferenceHelperImpl {
        return PreferenceHelperImpl(group)
    }

    fun createWidgetHelper(activity: Activity, widgetGroup: WidgetGroup): WidgetHelper {
        return WidgetHelper.with(activity, widgetGroup).let {
            it.dragStrokeWidth = activity.resources.dp(20F)
            it.selectedBorderWidth = activity.resources.dp(2F)
            it.touchPointRadius = activity.resources.dp(5F)
            it.selectedColor = ContextCompat.getColor(activity, R.color.colorPrimary)
            it.focusColor = ContextCompat.getColor(activity, R.color.colorAccent)
            it.pendingLayoutTime = 800L
            it.isPortrait = activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            it.canDrag = false
            it.isAutoInverted = activity.isAutoInverted
            it.isAutoLight = activity.isAutoLight
            it.isInverted = activity.isInverted
            it
        }
    }

    class PreferenceHelperImpl(private val group: RecyclerView) {
        private val infoList = ArrayList<PreferenceInfo<*>>()

        private val context = group.context

        init {
            group.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            val adapter = PreferenceAdapter(infoList, LayoutInflater.from(context)) {info, newValue ->
                onInfoChange(info, newValue)
            }
            group.adapter = adapter
            update()
        }

        fun update() {
            getPreferenceInfo(context, infoList)
            group.adapter?.notifyDataSetChanged()
        }

        private fun onInfoChange(info: PreferenceInfo<*>, newValue: Any) {
            when (info.key) {
                KEY_AUTO_LIGHT -> {
                    val v = newValue as? Boolean ?: true
                    context.isAutoLight = v
                }
                KEY_AUTO_INVERTED -> {
                    val v = newValue as? Boolean ?: true
                    context.isAutoInverted = v
                }
                KEY_INVERTED -> {
                    val v = newValue as? Boolean ?: true
                    context.isInverted = v
                }
                KEY_GRID_SIZE -> {
                    val v = newValue as? Int ?: DEF_GRID_SIZE
                    context.gridSize = v
                }
                KEY_ORIENTATION -> {
                    val v = newValue as? Int ?: PreferenceChoice.VALUE_NEUTRAL
                    context.clockOrientation = when (v) {
                        PreferenceChoice.VALUE_POSITIVE -> ORIENTATION_PORTRAIT
                        PreferenceChoice.VALUE_NEGATIVE -> ORIENTATION_LANDSCAPE
                        else -> ORIENTATION_AUTO
                    }
                }
            }
        }

    }

    private fun getPreferenceInfo(context: Context, list: ArrayList<PreferenceInfo<*>>) {
        list.clear()
        list.add(PreferenceBoolean().apply {
            key = KEY_AUTO_LIGHT
            value = context.isAutoLight
            title = context.getString(R.string.pref_title_auto_light)
            summerOfTrue = context.getString(R.string.pref_summer_t_auto_light)
            summerOfFalse = context.getString(R.string.pref_summer_f_auto_light)
        })
        list.add(PreferenceBoolean().apply {
            key = KEY_AUTO_INVERTED
            value = context.isAutoInverted
            title = context.getString(R.string.pref_title_auto_inverted)
            summerOfTrue = context.getString(R.string.pref_summer_t_auto_inverted)
            summerOfFalse = context.getString(R.string.pref_summer_f_auto_inverted)
        })
        list.add(PreferenceBoolean().apply {
            key = KEY_INVERTED
            value = context.isInverted
            title = context.getString(R.string.pref_title_inverted)
            summerOfTrue = context.getString(R.string.pref_summer_t_inverted)
            summerOfFalse = context.getString(R.string.pref_summer_f_inverted)
        })
        list.add(PreferenceNumber().apply {
            key = KEY_GRID_SIZE
            value = context.gridSize
            min = MIN_GRID_SIZE
            max = MAX_GRID_SIZE
            title = context.getString(R.string.pref_title_grid_size)
            summer = context.getString(R.string.pref_summer_grid_size)
        })
        list.add(PreferenceChoice().apply {
            key = KEY_ORIENTATION
            value = when (context.clockOrientation) {
                ORIENTATION_PORTRAIT -> PreferenceChoice.VALUE_POSITIVE
                ORIENTATION_LANDSCAPE -> PreferenceChoice.VALUE_NEGATIVE
                else -> PreferenceChoice.VALUE_NEUTRAL
            }
            title = context.getString(R.string.pref_title_orientation)
            summer = context.getString(R.string.pref_summer_orientation)

            positiveIcon = R.drawable.ic_screen_lock_portrait_white_24dp
            negativeIcon = R.drawable.ic_screen_lock_landscape_white_24dp
            neutralIcon = R.drawable.ic_screen_rotation_white_24dp

            positiveName = context.getString(R.string.pref_orientation_protrait)
            negativeName = context.getString(R.string.pref_orientation_landscape)
            neutralName = context.getString(R.string.pref_orientation_auto)
        })
    }

}

var Context.isAutoLight: Boolean
    get() {
        return getPreferences(PreferenceHelper.KEY_AUTO_LIGHT, true)
    }
    set(value) {
        putPreferences(PreferenceHelper.KEY_AUTO_LIGHT, value)
    }

var Context.isAutoInverted: Boolean
    get() {
        return getPreferences(PreferenceHelper.KEY_AUTO_INVERTED, true)
    }
    set(value) {
        putPreferences(PreferenceHelper.KEY_AUTO_INVERTED, value)
    }

var Context.isInverted: Boolean
    get() {
        return getPreferences(PreferenceHelper.KEY_INVERTED, true)
    }
    set(value) {
        putPreferences(PreferenceHelper.KEY_INVERTED, value)
    }

var Context.gridSize: Int
    get() {
        return getPreferences(PreferenceHelper.KEY_GRID_SIZE, PreferenceHelper.DEF_GRID_SIZE)
    }
    set(value) {
        putPreferences(PreferenceHelper.KEY_GRID_SIZE, value)
    }

var Context.clockOrientation: Int
    get() {
        return getPreferences(PreferenceHelper.KEY_ORIENTATION, PreferenceHelper.ORIENTATION_AUTO)
    }
    set(value) {
        putPreferences(PreferenceHelper.KEY_ORIENTATION, value)
    }

inline fun <reified T> Context.getPreferences(name: String, def: T): T {
    val sp = PreferenceManager.getDefaultSharedPreferences(this)
    return when(def) {
        is String -> {
            sp.getString(name, def) as T
        }
        is Float -> {
            sp.getFloat(name, def) as T
        }
        is Long -> {
            sp.getLong(name, def) as T
        }
        is Int -> {
            sp.getInt(name, def) as T
        }
        is Boolean -> {
            sp.getBoolean(name, def) as T
        }
        else -> def
    }
}

inline fun <reified T> Context.putPreferences(name: String, value: T) {
    putPreferences(Part(name, value))
}

fun Context.putPreferences(vararg infos: Part<*>) {
    val sp = PreferenceManager.getDefaultSharedPreferences(this)
    val edit = sp.edit()
    for (info in infos) {
        when(info.value) {
            is String -> {
                edit.putString(info.name, info.value)
            }
            is Float -> {
                edit.putFloat(info.name, info.value)
            }
            is Long -> {
                edit.putLong(info.name, info.value)
            }
            is Int -> {
                edit.putInt(info.name, info.value)
            }
            is Boolean -> {
                edit.putBoolean(info.name, info.value)
            }
            else -> {
                edit.putString(info.name, info.value.toString())
            }
        }
    }
    edit.apply()
}

data class Part<T>(val name: String, val value: T)