package liang.lollipop.electronicclock.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_panel_info_adjustment.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.fragment.PanelInfoAdjustmentFragment
import liang.lollipop.electronicclock.utils.BroadcastHelper
import liang.lollipop.electronicclock.utils.PanelInfoAdjustmentHelper
import liang.lollipop.widget.utils.doAsync
import liang.lollipop.electronicclock.utils.gridSize
import liang.lollipop.electronicclock.view.AutoSeekBar
import liang.lollipop.guidelinesview.util.lifecycleBinding
import liang.lollipop.guidelinesview.util.onEnd
import liang.lollipop.guidelinesview.util.onStart
import liang.lollipop.widget.utils.DatabaseHelper
import liang.lollipop.widget.utils.Utils
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject
import kotlin.math.max

class PanelInfoAdjustmentActivity : BottomNavigationActivity(),
    PanelInfoAdjustmentFragment.InfoLoadCallback,
    PanelInfoAdjustmentFragment.PanelSizeChangeCallback,
    AutoSeekBar.OnProgressChangeListener{

    override val contentViewId: Int
        get() = R.layout.activity_panel_info_adjustment

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
                setClassName("liang.lollipop.electronicclock", PanelInfoAdjustmentActivity::class.java.name)
                putExtra(ARG_PANEL_TYPE, typeInt)
                putExtra(ARG_PANEL_ID, info.id)
                val jsonObject = JSONObject()
                info.serialize(jsonObject)
                putExtra(ARG_PANEL_INFO, jsonObject.toString())
            }
        }

        fun getInfo(intent: Intent): String {
            return intent.getStringExtra(ARG_PANEL_INFO)?:""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        infoId = intent.getIntExtra(ARG_PANEL_ID, PanelInfo.NO_ID)
        initView()
    }

    private fun initView() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = PanelInfoAdjustmentHelper.createFragmentForIntent(intent, ARG_PANEL_TYPE, infoId)
        fragment.putInfoValue(getInfo(intent))
        adjustmentFragment = fragment
        transaction.add(R.id.adjustmentFragmentGroup, fragment, TAG_FRAGMENT)
        transaction.commit()

        showFAB(R.drawable.ic_done_black_24dp) {
            it.setOnClickListener {
                callSubmit()
            }
        }
        invertedBtn.setOnClickListener {
            if (isInvertedColor) {
                previewGroup.setBackgroundColor(Color.TRANSPARENT)
            } else {
                previewGroup.setBackgroundColor(Color.WHITE)
            }
            isInvertedColor = !isInvertedColor
        }
        bindSeekBarAnimation()
        spanXSeekBar.min = 1F
        spanYSeekBar.min = 1F
        spanXSeekBar.max = gridSize.toFloat()
        spanYSeekBar.max = spanXSeekBar.max
        spanXSeekBar.onProgressChangeListener = this
        spanYSeekBar.onProgressChangeListener = this

        setPanelSize(1, 1)
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
        sizeChangeBtn.setOnClickListener {
            if (isSeekBarShown) {
                spanXSeekBar.animate()
                    .translationY(spanXSeekBar.height.toFloat())
                    .alpha(0F)
                    .lifecycleBinding {
                        onEnd {
                            spanXSeekBar.visibility = View.INVISIBLE
                            removeThis(it)
                        }
                    }.start()

                spanYSeekBar.animate()
                    .translationX(spanYSeekBar.width * -1F)
                    .alpha(0F)
                    .lifecycleBinding {
                        onEnd {
                            spanYSeekBar.visibility = View.INVISIBLE
                            removeThis(it)
                        }
                    }.start()
            } else {
                spanXSeekBar.animate()
                    .translationY(0F)
                    .alpha(1F)
                    .lifecycleBinding {
                        onStart {
                            spanXSeekBar.visibility = View.VISIBLE
                        }
                        onEnd {
                            removeThis(it)
                        }
                    }.start()

                spanYSeekBar.animate()
                    .translationX(0F)
                    .alpha(1F)
                    .lifecycleBinding {
                        onStart {
                            spanYSeekBar.visibility = View.VISIBLE
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
            spanXSeekBar, spanYSeekBar -> {
                onPanelSizeChange(spanXSeekBar.progress.toInt(), spanYSeekBar.progress.toInt())
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
            previewGroup.addView(fragment.getPanelView())
            fragment.getPanelInfo().let {
                setPanelSize(it.spanX, it.spanY)
            }
        }
    }

    override fun getSizeChangeCallback(): PanelInfoAdjustmentFragment.PanelSizeChangeCallback {
        return this
    }

    override fun setPanelSize(spanX: Int, spanY: Int) {
        spanXSeekBar.setProgress(spanX.toFloat(), false)
        spanYSeekBar.setProgress(spanY.toFloat(), false)
        onPanelSizeChange(spanX, spanY)
    }

    private fun onPanelSizeChange(spanX: Int, spanY: Int) {
        val x = max(spanX, 1)
        val y = max(spanY, 1)
        previewGroup.changeSize(x, y)
        sizeValueView.text = "$x * $y"
    }

}
