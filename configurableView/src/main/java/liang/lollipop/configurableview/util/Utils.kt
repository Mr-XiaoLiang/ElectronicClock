package liang.lollipop.configurableview.util

import android.view.View

/**
 * @author lollipop
 * @date 2019-10-20 00:22
 * 工具的入口方法
 */

/**
 * 获取一个ID管理器，这个ID管理器是相互独立的
 * 但是ID的生成是基于View.generateViewId()
 * 因此id本身是全局不重复的，目的在于，
 * 希望每一个id管理器下的同名view的id不同
 */
fun getIdManager(): IdManager {
    return IdManager()
}

class IdManager {

    private val idMap = HashMap<String, Int>()

    fun createId(name: String): Int {
        idMap[name]?.let {
            return it
        }
        val id = View.generateViewId()
        idMap[name] = id
        return id
    }

    fun getIdByName(name: String): Int {
        idMap[name]?.let {
            return it
        }
        return 0
    }

}