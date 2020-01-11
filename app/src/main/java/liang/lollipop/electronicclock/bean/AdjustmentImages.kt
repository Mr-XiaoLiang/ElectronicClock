package liang.lollipop.electronicclock.bean

import android.net.Uri

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整padding类型
 */
class AdjustmentImages: AdjustmentInfo() {

    val images = ArrayList<Uri>()

    var maxSize = -1

    fun reset(p: ArrayList<Uri>) {
        images.clear()
        images.addAll(p)
    }

    fun resetFromString(p: ArrayList<String>) {
        images.clear()
        p.forEach {
            images.add(Uri.parse(it))
        }
    }

    fun reset(p: Array<Uri>) {
        images.clear()
        images.addAll(p)
    }

    override fun copy(info: AdjustmentInfo) {
        super.copy(info)
        if (info is AdjustmentImages) {
            reset(info.images)
        }
    }

}