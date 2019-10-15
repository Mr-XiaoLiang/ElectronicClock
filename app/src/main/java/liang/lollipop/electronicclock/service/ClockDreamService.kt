package liang.lollipop.electronicclock.service

import android.service.dreams.DreamService
import liang.lollipop.electronicclock.R

/**
 * @author lollipop
 * @date 2019-10-15 23:54
 * 时钟的屏保服务
 *
 */
class ClockDreamService: DreamService() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setContentView(R.layout.activity_widget)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()

    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}