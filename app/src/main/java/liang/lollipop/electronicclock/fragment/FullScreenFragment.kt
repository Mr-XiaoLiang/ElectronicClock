package liang.lollipop.electronicclock.fragment

/**
 * @author lollipop
 * @date 2019-08-07 21:47
 * 全屏Fragment的接口，它会收到来自Activity的inset事件
 */
interface FullScreenFragment {

    fun onWindowInsetsChange(left: Int, top: Int, right: Int, bottom: Int)

}