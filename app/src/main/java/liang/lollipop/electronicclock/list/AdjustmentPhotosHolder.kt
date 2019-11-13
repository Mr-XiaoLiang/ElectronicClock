package liang.lollipop.electronicclock.list

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.activity.ImageSelectActivity
import liang.lollipop.electronicclock.bean.AdjustmentImages

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 * 调整项的接口
 */
class AdjustmentPhotosHolder(view: View): AdjustmentHolder<AdjustmentImages>(view),
    AdjustmentRequestActivityHolder {

    companion object {
        fun create(inflater: LayoutInflater, group: ViewGroup): AdjustmentPhotosHolder {
            return AdjustmentPhotosHolder(
                inflater.inflate(R.layout.item_adjustment_photos, group, false))
        }

        private const val REQUEST_CODE = 4069
    }

    private val titleView: TextView = view.findViewById(R.id.titleView)
    private val summaryView: TextView = view.findViewById(R.id.summaryView)
    private val valueView: TextView = view.findViewById(R.id.valueView)
    private val valueIcon: ImageView = view.findViewById(R.id.photoIcon)

    private var requestCallback: ((AdjustmentRequestActivityHolder, Intent, Int) -> Unit)? = null

    private var bindInfo: AdjustmentImages? = null

    init {
        view.setOnClickListener {
            requestCallback?.invoke(this,
                ImageSelectActivity.createIntent(
                    view.context,
                    bindInfo?.maxSize?:36),
                REQUEST_CODE)
        }
    }

    override fun bindRequestActivityCallback(callback: (AdjustmentRequestActivityHolder, Intent, Int) -> Unit) {
        requestCallback = callback
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE
            && resultCode == Activity.RESULT_OK) {
            val uriList = ImageSelectActivity.getUriList(data)
            bindInfo?.reset(uriList)
            updateSize(uriList.size)
            onValueChangeListener?.onValueChange(this, uriList)
        }
    }

    override fun onBind(info: AdjustmentImages) {
        bindInfo = info
        setViewEnable(info.enable, titleView, valueView, valueIcon, summaryView)
        titleView.text = info.title
        summaryView.text = info.summary
        updateSize(info.images.size)
    }

    private fun updateSize(size: Int) {
        valueView.text = size.let {
            if (it < 100) {
                "$it"
            } else {
                "99"
            }
        }
    }

}