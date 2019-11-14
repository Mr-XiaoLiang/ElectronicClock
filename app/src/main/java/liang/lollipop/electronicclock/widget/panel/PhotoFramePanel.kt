package liang.lollipop.electronicclock.widget.panel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.material.card.MaterialCardView
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.widget.info.PhotoFramePanelInfo
import liang.lollipop.widget.widget.Panel

/**
 * @author lollipop
 * @date 2019-10-28 22:01
 * 相框的面板
 */
class PhotoFramePanel(info: PhotoFramePanelInfo): Panel<PhotoFramePanelInfo>(info) {

    private var photoAdapter: PhotoAdapter? = null

    override fun createView(context: Context): View {
        val cardView = FullCardView(context)
        val recyclerView = RecyclerView(context)
        cardView.addView(recyclerView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        recyclerView.layoutManager = LinearLayoutManager(context,
            RecyclerView.HORIZONTAL, false)
        photoAdapter = PhotoAdapter(panelInfo.images, context)
        recyclerView.adapter = photoAdapter
        PagerSnapHelper().attachToRecyclerView(recyclerView)
        return cardView
    }

    override fun onInfoChange() {
        super.onInfoChange()
        tryMyView<FullCardView> {
            it.radius = panelInfo.radius
            it.cardElevation = panelInfo.elevation
            photoAdapter?.notifyDataSetChanged()
            it.requestLayout()
        }
    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        tryMyView<MaterialCardView> {
            it.setCardBackgroundColor(color)
        }
    }

    private class PhotoAdapter(private val data: ArrayList<String>,
                               private val context: Context) : RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            return PhotoHolder.create(context)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.onBind(data[position])
        }

    }

    private class PhotoHolder private constructor(private val view: ImageView): RecyclerView.ViewHolder(view) {
        companion object {
            fun create(context: Context): PhotoHolder {
                return PhotoHolder(ImageView(context).apply {
                    layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                })
            }

            private val glideOption = RequestOptions().error(R.drawable.ic_broken_image_white_24dp)
            private val fadeFactory = DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build()
        }

        fun onBind(uri: String) {
            Glide.with(view)
                .load(uri)
                .apply(glideOption)
                .transition(DrawableTransitionOptions
                    .with(fadeFactory))
                .into(view)
        }

    }

    private class FullCardView(context: Context): MaterialCardView(context) {
        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            val childWidth = right - left
            val childHeight = bottom - top
            for (index in 0 until childCount) {
                getChildAt(index).layout(0, 0, childWidth, childHeight)
            }
        }

        override fun requestLayout() {
            super.requestLayout()
            for (index in 0 until childCount) {
                getChildAt(index).requestLayout()
            }
        }

    }

}