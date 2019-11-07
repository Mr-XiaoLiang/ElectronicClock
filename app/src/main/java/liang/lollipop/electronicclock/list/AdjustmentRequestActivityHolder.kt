package liang.lollipop.electronicclock.list

import android.content.Intent

/**
 * @author lollipop
 * @date 2019-11-07 22:35
 * 会发起Activity请求的holder
 */
interface AdjustmentRequestActivityHolder {

    fun bindRequestActivityCallback(callback: (AdjustmentRequestActivityHolder, Intent, Int) -> Unit)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

}