package liang.lollipop.electronicclock.activity

import android.animation.Animator
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import liang.lollipop.electronicclock.R
import liang.lollipop.guidelinesview.util.*


/**
 * 图片选择的页面
 * @author Lollipop
 */
class ImageSelectActivity : BottomNavigationActivity() {

    companion object {
        private const val LOADER_ID = 456
    }

    override val contentViewId: Int
        get() = R.layout.activity_select

    private var maxSize = 0

    private val imageList = ArrayList<ImageBean>()
    private val selectedList = ArrayList<ImageBean>()

    private val loaderCallback = object : LoaderManager.LoaderCallbacks<Cursor> {

        private val SIZE = MediaStore.Images.Media.SIZE
        private val MIME_TYPE = MediaStore.Images.Media.MIME_TYPE
        private val DATE_ADDED = MediaStore.Images.Media.DATE_ADDED

        private val IMAGE_PROJECTION = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MIME_TYPE,
            SIZE,
            MediaStore.Images.Media._ID
        )

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            return CursorLoader(this@ImageSelectActivity,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_PROJECTION, "$SIZE > 0 AND $MIME_TYPE = ? OR $MIME_TYPE = ? ",
                arrayOf("image/jpeg", "image/png"),"$DATE_ADDED DESC")
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            data?:return
            if (data.count < 1) {
                onLoaded()
                return
            }
            val id = data.getColumnIndex(MediaStore.Images.Media._ID)
            val name = data.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            imageList.clear()
            while (data.moveToNext()) {
                val imageId = data.getLong(id)
                val imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "$imageId")
                val imageName = data.getString(name)
                imageList.add(ImageBean(imageUri, imageName))
            }
            onLoaded()
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            initImages()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFAB(R.drawable.ic_done_black_24dp) {

        }
        initImages()
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, loaderCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        LoaderManager.getInstance(this).destroyLoader(LOADER_ID)
    }

    private fun initImages() {
        clearList()
        startContentLoading()
    }

    private fun onLoaded() {
        // TODO
        stopContentLoading()
    }

    private fun clearList() {
        selectedList.clear()
        imageList.clear()
    }

    private data class ImageBean(val uri: Uri, val name: String)

    private class ImageHolder
        private constructor(view: View,
                            private val isChecked: (ImageHolder) -> Boolean,
                            private val onClick: (ImageHolder) -> Boolean): RecyclerView.ViewHolder(view) {
        companion object {
            fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup,
                             isChecked: (ImageHolder) -> Boolean,
                             onClick: (ImageHolder) -> Boolean): ImageHolder {
                return ImageHolder(layoutInflater.inflate(
                    R.layout.item_image_select, parent, false), isChecked, onClick)
            }
        }

        private val photoView: ImageView = view.findViewById(R.id.photoView)
        private val checkboxView: View = view.findViewById(R.id.checkboxView)

        init {
            view.setOnClickListener {
                if (onClick(this)) {
                    startAnimation()
                }
            }
        }

        private var animator: Animator? = null

        fun onBind(bean: ImageBean) {
            Glide.with(photoView).load(bean.uri).into(photoView)
            cancelAnimation()
            checkboxView.visibility = if (isChecked(this)) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        private fun cancelAnimation() {
            animator?.cancel()
            animator = null
        }

        private fun startAnimation() {
            animator = if (checkboxView.visibility == View.VISIBLE) {
                checkboxView.revealCloseWith(checkboxView) {
                    onEnd {
                        checkboxView.visibility = View.INVISIBLE
                        removeThis(it)
                    }
                    onCancel {
                        removeThis(it)
                    }
                }
            } else {
                checkboxView.revealOpenWith(checkboxView) {
                    onStart {
                        checkboxView.visibility = View.VISIBLE
                    }
                    onEnd {
                        removeThis(it)
                    }
                    onCancel {
                        removeThis(it)
                    }
                }
            }
        }

    }
}
