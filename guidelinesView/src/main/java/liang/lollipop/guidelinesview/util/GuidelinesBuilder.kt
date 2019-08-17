package liang.lollipop.guidelinesview.util

import android.app.Activity
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.ContentFrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import liang.lollipop.guidelinesview.view.GuidelinesView

/**
 * @author lollipop
 * @date 2019-08-17 02:07
 * 引导过程的构造类
 */
class GuidelinesBuilder(val target: View) {

    /**
     * 面板的半径
     * 面板是圆形，它的半径默认情况下是容器的短边的1倍
     */
    var panelRadius = GuidelinesInfo.panelRadius

    /**
     * 默认的内补白
     * 用于间隔面板和目标View之间的距离
     */
    var paddingSize = GuidelinesInfo.paddingSize

    /**
     * 面板的颜色
     */
    var panelColor = GuidelinesInfo.panelColor

    /**
     * 背景色
     */
    var backgroundColor = GuidelinesInfo.backgroundColor

    /**
     * 文字的颜色
     */
    var fontColor = GuidelinesInfo.fontColor

    /**
     * 文字大小
     */
    var fontSize = GuidelinesInfo.fontSize

    /**
     * 下一个引导
     */
    private var nextGuidelines: GuidelinesBuilder? = null

    /**
     * 上一个
     */
    private var previousGuidelines: GuidelinesBuilder? = null

    /**
     * 内容
     */
    var message = ""

    /**
     * 显示的容器
     */
    private var group: ViewGroup? = null

    /**
     * 消息所在的位置
     */
    var messageGravity = Location.Auto

    /**
     * 引导显示的View
     */
    private var guidelinesView: GuidelinesView? = null

    /**
     * 动画持续时间
     */
    var animationDuration = GuidelinesInfo.animationDuration

    /**
     * 关闭时的监听器
     */
    private var closeListener: (() -> Unit)? = null

    val targetParent: ViewGroup
        get() {
            val parent = target.parent ?: throw IllegalArgumentException("Target view has no parent")
            if (parent is ViewGroup) {
                return parent
            } else {
                throw IllegalArgumentException("The parent of the target View is not a ViewGroup")
            }
        }

    fun showIn(activity: Activity): GuidelinesBuilder {
        val contentParent = activity.findViewById<ViewGroup>(android.R.id.content)?:throw RuntimeException("can`t found root view group")
        group = contentParent
        return this
    }

    fun showIn(fragment: Fragment): GuidelinesBuilder {
        val rootView = fragment.view as? ViewGroup ?: throw RuntimeException("can`t found root view group")
        if (rootView is FrameLayout || rootView is CoordinatorLayout) {
            group = rootView
        } else {
            throw RuntimeException("can`t found root view group")
        }
        return this
    }

    fun showInParent(): GuidelinesBuilder {
        var view = target
        var viewGroup: ViewGroup
        do {
            val parent = view.parent ?: RuntimeException("can`t found view group")
            viewGroup = parent as ViewGroup
            view = viewGroup
        } while (viewGroup !is FrameLayout && viewGroup !is CoordinatorLayout)
        group = viewGroup
        return this
    }

    fun value(msg: String): GuidelinesBuilder {
        message = msg
        return this
    }

    fun value(msgId: Int): GuidelinesBuilder {
        return value(target.context.getString(msgId))
    }

    fun onClose(run: () -> Unit): GuidelinesBuilder {
        closeListener = run
        return this
    }

    private fun showGuidelines() {
        panelRadius.isLocked = true
        paddingSize.isLocked = true
        if (group == null) {
            showInParent()
        }
        val viewGroup = group ?: throw IllegalArgumentException("Guidelines view has no parent")
        val view = getGuidelinesView()
        bindClickListener(view)
        if (view.parent != null && view.parent != viewGroup) {
            (view.parent as ViewGroup).removeView(view)
        }
        if (view.parent == null) {
            viewGroup.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        view.bringToFront()
        view.show()
    }

    private fun bindClickListener(view: GuidelinesView) {
        view.setOnClickListener {
            closeListener?.invoke()

            view.onAnimationHideEnd = {
                view.onAnimationHideEnd = null

                nextGuidelines?.let {
                    if (it.group == null) {
                        it.group = group
                    }
                    if (it.guidelinesView == null) {
                        it.guidelinesView = view
                    }
                    it.show()
                }
            }
            view.hide()
            view.setOnClickListener(null)
        }
    }

    private fun getGuidelinesView(): GuidelinesView {
        return if (guidelinesView != null) {
            guidelinesView!!.builder = this
            guidelinesView!!
        } else {
            val view = GuidelinesView(target.context)
            view.builder = this
            guidelinesView = view
            view
        }
    }

    fun next(target: View): GuidelinesBuilder {
        val builder = GuidelinesBuilder(target)
        nextGuidelines = builder
        builder.previousGuidelines = this
        return builder
    }

    fun show() {
        if (previousGuidelines != null) {
            previousGuidelines!!.show()
            previousGuidelines = null
            return
        }
        target.post {
            showGuidelines()
        }
    }

}