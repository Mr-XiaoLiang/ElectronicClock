package liang.lollipop.electronicclock.activity

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.PermissionChecker
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_select.*
import liang.lollipop.electronicclock.R
import liang.lollipop.guidelinesview.util.*


/**
 * 图片选择的页面
 * @author Lollipop
 */
class ImageSelectActivity : BottomNavigationActivity() {

    companion object {
        private const val LOADER_ID = 456

        private const val REQUEST_PERMISSION = 223

        private const val ARG_MAX_SIZE = "ARG_MAX_SIZE"

        private const val ARG_RESULT_URI = "ARG_RESULT_URI"

        fun selectedForResult(activity: Activity, requestId: Int, maxSize: Int = -1) {
            activity.startActivityForResult(Intent(activity, ImageSelectActivity::class.java).apply {
                putExtra(ARG_MAX_SIZE, maxSize)
            }, requestId)
        }

        fun getUriList(data: Intent?): Array<Uri> {
            data?:return Array(0) { Uri.EMPTY }
            val uriList = data.getStringArrayListExtra(ARG_RESULT_URI)?:return Array(0) { Uri.EMPTY }
            return Array(uriList.size) { i -> Uri.parse(uriList[i]) }
        }
    }

    override val contentViewId: Int
        get() = R.layout.activity_select

    private var maxSize = 0

    private val imageList = ArrayList<ImageBean>()
    private val selectedList = ArrayList<ImageBean>()

    private val adapter: ImageAdapter by lazy {
        ImageAdapter(imageList, layoutInflater,
            { isItemChecked(it) },
            { onItemClick(it) })
    }

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
            imageList.clear()
            if (data == null || data.count < 1) {
                onLoaded()
                return
            }
            val id = data.getColumnIndex(MediaStore.Images.Media._ID)
            val name = data.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
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

        maxSize = intent.getIntExtra(ARG_MAX_SIZE, -1)

        showFAB(R.drawable.ic_done_black_24dp) { fab ->
            fab.setOnClickListener {
                if (selectedList.isEmpty()) {
                    Snackbar.make(imageListView, R.string.toast_result_empty, Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                setResult(Activity.RESULT_OK, Intent().apply {
                    val uris = ArrayList<String>()
                    for (bean in selectedList) {
                        uris.add(bean.uri.toString())
                    }
                    putExtra(ARG_RESULT_URI, uris)
                })
                onBackPressed()
            }
        }

        imageListView.layoutManager = GridLayoutManager(this, 4,
            RecyclerView.VERTICAL, false)
        imageListView.adapter = adapter
        adapter.notifyDataSetChanged()

        initImages()

        when (PermissionChecker.checkCallingOrSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionChecker.PERMISSION_GRANTED -> {
                bindLoader()
            }
            PermissionChecker.PERMISSION_DENIED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION)
                }
            }
            else -> {
                clearList()
                onLoaded()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSION -> {
                val index = permissions.indexOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (grantResults[index] == PermissionChecker.PERMISSION_GRANTED) {
                    bindLoader()
                } else {
                    alert {
                        setMessage(R.string.refuse_read_picture)
                        show()
                    }
                    clearList()
                    onLoaded()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LoaderManager.getInstance(this).destroyLoader(LOADER_ID)
    }

    private fun bindLoader() {
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, loaderCallback)
    }

    private fun isItemChecked(bean: ImageBean): Boolean {
        return selectedList.indexOf(bean) > 0
    }

    private fun onItemClick(position: Int): Boolean {
        val bean = imageList[position]
        if (isItemChecked(bean)) {
            selectedList.remove(bean)
            selectedChange()
            return true
        }
        if (maxSize < 1 || selectedList.size < maxSize) {
            selectedList.add(bean)
            selectedChange()
            return true
        }
        Snackbar.make(imageListView, R.string.toast_already_max, Snackbar.LENGTH_LONG).show()
        return false
    }

    private fun selectedChange() {
        sizeView.text = if (maxSize > 0) {
            "${selectedList.size} / $maxSize"
        } else {
            "${selectedList.size}"
        }
    }

    private fun initImages() {
        clearList()
        selectedChange()
        startContentLoading()
    }

    private fun onLoaded() {
        stopContentLoading()
        if (imageList.isEmpty()) {
            emptyIcon.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        } else {
            emptyIcon.visibility = View.INVISIBLE
            emptyText.visibility = View.INVISIBLE

            imageListView.layoutManager?.let { manager ->
                if (manager is GridLayoutManager) {
                    manager.spanCount = when {
                        imageList.size < 10 -> 2
                        imageList.size < 100 -> 3
                        imageList.size < 200 -> 4
                        else -> 5
                    }
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun clearList() {
        selectedList.clear()
        imageList.clear()
    }

    private class ImageAdapter(
        private val data: ArrayList<ImageBean>,
        private val layoutInflater: LayoutInflater,
        private val isChecked: (ImageBean) -> Boolean,
        private val onClick: (Int) -> Boolean): RecyclerView.Adapter<ImageHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
            return ImageHolder.createHolder(layoutInflater, parent,
                {holder -> isChecked(data[holder.adapterPosition])},
                {holder -> onClick(holder.adapterPosition)})
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ImageHolder, position: Int) {
            holder.onBind(data[position])
        }

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

            private val glideOption = RequestOptions().error(R.drawable.ic_broken_image_white_24dp)
            private val fadeFactory = DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
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
            Glide.with(photoView)
                .load(bean.uri)
                .apply(glideOption)
                .transition(DrawableTransitionOptions.with(fadeFactory))
                .into(photoView)
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
