package liang.lollipop.electronicclock.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_panel_info_adjustment.*
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.fragment.BatteryAdjustmentFragment
import liang.lollipop.electronicclock.fragment.EmptyAdjustmentFragment
import liang.lollipop.electronicclock.fragment.PanelInfoAdjustmentFragment
import liang.lollipop.electronicclock.widget.info.BatteryInfo
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

class PanelInfoAdjustmentActivity : BottomNavigationActivity(),
    PanelInfoAdjustmentFragment.InfoLoadCallback,
    PanelInfoAdjustmentFragment.PanelSizeChangeCallback {

    override val contentViewId: Int
        get() = R.layout.activity_panel_info_adjustment

    private var infoId = PanelInfo.NO_ID

    private var adjustmentFragment: PanelInfoAdjustmentFragment? = null

    companion object {

        private const val ARG_PANEL_TYPE = "ARG_PANEL_TYPE"

        private const val ARG_PANEL_INFO = "ARG_PANEL_INFO"

        private const val ARG_PANEL_ID = "ARG_PANEL_ID"

        private const val TAG_FRAGMENT = "TAG_FRAGMENT"

        fun getIntent(info: PanelInfo): Intent {
            val typeInt = when (info) {
                is BatteryInfo -> PanelType.Battery.value
                else -> PanelType.Empty.value
            }
            return Intent().apply {
                setClassName("liang.lollipop.electronicclock", PanelInfoAdjustmentActivity::class.java.name)
                putExtra(ARG_PANEL_TYPE, typeInt)
                putExtra(ARG_PANEL_ID, info.id)
            }
        }

        fun getInfo(intent: Intent): String {
            return intent.getStringExtra(ARG_PANEL_INFO)?:""
        }
    }

    enum class PanelType(val value: Int) {
        Empty(0),
        Battery(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        infoId = intent.getIntExtra(ARG_PANEL_ID, PanelInfo.NO_ID)
        initView()
    }

    private fun initView() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = createFragmentByType(intent.getIntExtra(ARG_PANEL_TYPE, PanelType.Empty.value))
        adjustmentFragment = fragment
        transaction.add(R.id.adjustmentFragmentGroup, fragment, TAG_FRAGMENT)
        transaction.commit()

        showFAB(R.drawable.ic_done_black_24dp) {
            it.setOnClickListener {
                val result = Intent()
                val json = JSONObject()
                adjustmentFragment?.getPanelInfo()?.serialize(json)
                result.putExtra(ARG_PANEL_INFO, json.toString())
                setResult(Activity.RESULT_OK, result)
                onBackPressed()
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
        val panelView = adjustmentFragment?.getPanelView()?:return
        previewGroup.addView(panelView)
    }

    override fun getSizeChangeCallback(): PanelInfoAdjustmentFragment.PanelSizeChangeCallback {
        return this
    }

    override fun setPanelSize(spanX: Int, spanY: Int) {
        previewGroup.changeSize(spanX, spanY)
    }

    private fun createFragmentByType(typeId: Int): PanelInfoAdjustmentFragment {
        return when (typeId) {
            PanelType.Battery.value -> {
                BatteryAdjustmentFragment.getInstance(infoId)
            }
            else -> {
                EmptyAdjustmentFragment.getInstance()
            }
        }
    }

}
