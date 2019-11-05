package liang.lollipop.electronicclock.bean

import android.net.Uri

/**
 * @author lollipop
 * @date 2019-09-01 17:50
 * 调整padding类型
 */
class AdjustmentImages(run: AdjustmentImages.() -> Unit):
    AdjustmentInfo() {

    val images = ArrayList<Uri>()

    init {
        run(this)
    }

    fun reset(p: ArrayList<Uri>) {
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