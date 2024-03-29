package liang.lollipop.electronicclock.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import liang.lollipop.base.lazyBind
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.databinding.ActivityPanelInfoAdjustmentBinding
import liang.lollipop.electronicclock.fragment.PanelInfoAdjustmentFragment
import liang.lollipop.electronicclock.utils.BroadcastHelper
import liang.lollipop.electronicclock.utils.PanelInfoAdjustmentHelper
import liang.lollipop.electronicclock.utils.gridSize
import liang.lollipop.electronicclock.view.AutoSeekBar
import liang.lollipop.guidelinesview.util.lifecycleBinding
import liang.lollipop.guidelinesview.util.onEnd
import liang.lollipop.guidelinesview.util.onStart
import liang.lollipop.widget.utils.DatabaseHelper
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.utils.doAsync
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject
import kotlin.math.max

class PanelInfoAdjustmentActivity : BottomNavigationActivity(),
    PanelInfoAdjustmentFragment.InfoLoadCallback,
    PanelInfoAdjustmentFragment.PanelSizeChangeCallback,
    AutoSeekBar.OnProgressChangeListener {

    private val binding: ActivityPanelInfoAdjustmentBinding by lazyBind()

    private var infoId = PanelInfo.NO_ID

    private var adjustmentFragment: PanelInfoAdjustmentFragment? = null

    private var isSeekBarShown = true

    private var isInvertedColor = false

    private val logger = Utils.loggerI("PanelInfoAdjustmentActivity")

    companion object {

        private const val ARG_PANEL_TYPE = "ARG_PANEL_TYPE"

        private const val ARG_PANEL_INFO = "ARG_PANEL_INFO"

        private const val ARG_PANEL_ID = "ARG_PANEL_ID"

        private const val TAG_FRAGMENT = "TAG_FRAGMENT"

        fun getIntent(info: PanelInfo): Intent {
            val typeInt = PanelInfoAdjustmentHelper.getTypeByInfo(info)
            return Intent().apply {
                setClassName(
                    "liang.lollipop.electronicclock",
                    PanelInfoAdjustmentActivity::class.java.name
                )
                putExtra(ARG_PANEL_TYPE, typeInt)
                putExtra(ARG_PANEL_ID, info.id)
                val jsonObject = JSONObject()
                info.serialize(jsonObject)
                putExtra(ARG_PANEL_INFO, jsonObject.toString())
            }
        }

        fun getInfo(intent: Intent): String {
            return intent.getStringExtra(ARG_PANEL_INFO) ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        infoId = intent.getIntExtra(ARG_PANEL_ID, PanelInfo.NO_ID)
        initView()
    }

    private fun initView() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment =
            PanelInfoAdjustmentHelper.createFragmentForIntent(intent, ARG_PANEL_TYPE, infoId)
        fragment.putInfoValue(getInfo(intent))
        adjustmentFragment = fragment
        transaction.add(R.id.adjustmentFragmentGroup, fragment, TAG_FRAGMENT)
        transaction.commit()

        showFAB(R.drawable.ic_done_black_24dp) {
            it.setOnClickListener {
                callSubmit()
            }
        }
        binding.invertedBtn.setOnClickListener {
            isInvertedColor = !isInvertedColor
            onPreviewBackgroundChange()
        }
        bindSeekBarAnimation()
        binding.spanXSeekBar.min = 1F
        binding.spanYSeekBar.min = 1F
        binding.spanXSeekBar.max = gridSize.toFloat()
        binding.spanYSeekBar.max = binding.spanXSeekBar.max
        binding.spanXSeekBar.onProgressChangeListener = this
        binding.spanYSeekBar.onProgressChangeListener = this

        binding.previewGroup.setPadding(30F)

        setPanelSize(1, 1)
    }

    override fun createContentView(): View {
        return binding.root
    }

    private fun onPreviewBackgroundChange() {
        binding.previewGroup.setBackgroundColor(getPreviewBackgroundColor(isInvertedColor))
        adjustmentFragment?.onBackgroundColorChange(getPreviewBackgroundColor(!isInvertedColor))
    }

    private fun getPreviewBackgroundColor(isInverted: Boolean): Int {
        return if (isInverted) {
            Color.BLACK
        } else {
            Color.WHITE
        }
    }

    private fun callSubmit() {
        val fragment = adjustmentFragment
        if (fragment == null) {
            onBackPressed()
            return
        }
        startContentLoading()

        val result = Intent()
        val json = JSONObject()
        val info = fragment.getPanelInfo()
        info.id = infoId
        info.serialize(json)
        result.putExtra(ARG_PANEL_INFO, json.toString())
        setResult(Activity.RESULT_OK, result)
        // 没有ID就不做数据库更新了
        if (info.id == PanelInfo.NO_ID) {
            logger("info.id = NO_ID, no update")
            onBackPressed()
            return
        }
        doAsync {
            val helper = DatabaseHelper
                .write(this)
            helper.updateOnlyInfo(info) {
                runOnUiThread {
                    helper.close()
                    stopContentLoading()
                    BroadcastHelper.sendEmptyBroadcast(
                        this@PanelInfoAdjustmentActivity,
                        BroadcastHelper.ACTION_WIDGET_INFO_CHANGE
                    )
                    onBackPressed()
                }
            }
        }
    }

    private fun bindSeekBarAnimation() {
        binding.sizeChangeBtn.setOnClickListener {
            if (isSeekBarShown) {
                binding.spanXSeekBar.animate()
                    .translationY(binding.spanXSeekBar.height.toFloat())
                    .alpha(0F)
                    .lifecycleBinding {
                        onEnd {
                            binding.spanXSeekBar.visibility = View.INVISIBLE
                            removeThis(it)
                        }
                    }.start()

                binding.spanYSeekBar.animate()
                    .translationX(binding.spanYSeekBar.width * -1F)
                    .alpha(0F)
                    .lifecycleBinding {
                        onEnd {
                            binding.spanYSeekBar.visibility = View.INVISIBLE
                            removeThis(it)
                        }
                    }.start()
            } else {
                binding.spanXSeekBar.animate()
                    .translationY(0F)
                    .alpha(1F)
                    .lifecycleBinding {
                        onStart {
                            binding.spanXSeekBar.visibility = View.VISIBLE
                        }
                        onEnd {
                            removeThis(it)
                        }
                    }.start()

                binding.spanYSeekBar.animate()
                    .translationX(0F)
                    .alpha(1F)
                    .lifecycleBinding {
                        onStart {
                            binding.spanYSeekBar.visibility = View.VISIBLE
                        }
                        onEnd {
                            removeThis(it)
                        }
                    }.start()
            }
            isSeekBarShown = !isSeekBarShown
        }
    }

    override fun onProgressChange(view: AutoSeekBar, progress: Float) {
        when (view) {
            binding.spanXSeekBar, binding.spanYSeekBar -> {
                onPanelSizeChange(
                    binding.spanXSeekBar.progress.toInt(),
                    binding.spanYSeekBar.progress.toInt()
                )
                adjustmentFragment?.getPanelInfo()?.sizeChange(
                    binding.spanXSeekBar.progress.toInt(),
                    binding.spanYSeekBar.progress.toInt()
                )
            }
        }
    }

    override fun onInfoLoadStatusChange(isLoading: Boolean) {
        if (isLoading) {
            startContentLoading()
        } else {
            stopContentLoading()
        }
    }

    override fun onPanelInitComplete() {
        adjustmentFragment?.let { fragment ->
            binding.previewGroup.addView(fragment.getPanelView())
            fragment.getPanelInfo().let {
                setPanelSize(it.spanX, it.spanY)
            }
            onPreviewBackgroundChange()
        }
    }

    override fun getSizeChangeCallback(): PanelInfoAdjustmentFragment.PanelSizeChangeCallback {
        return this
    }

    override fun setPanelSize(spanX: Int, spanY: Int) {
        binding.spanXSeekBar.setProgress(spanX.toFloat(), false)
        binding.spanYSeekBar.setProgress(spanY.toFloat(), false)
        onPanelSizeChange(spanX, spanY)
    }

    @SuppressLint("SetTextI18n")
    private fun onPanelSizeChange(spanX: Int, spanY: Int) {
        val x = max(spanX, 1)
        val y = max(spanY, 1)
        binding.previewGroup.changeSize(x, y)
        binding.sizeValueView.text = "$x * $y"
    }

    override fun requestActivityForResult(intent: Intent, requestId: Int) {
        startActivityForResult(intent, requestId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adjustmentFragment?.resultFromActivity(requestCode, resultCode, data)
    }

}
