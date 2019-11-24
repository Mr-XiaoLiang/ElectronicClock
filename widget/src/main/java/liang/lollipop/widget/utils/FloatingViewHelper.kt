package liang.lollipop.widget.utils

import android.content.Context
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.ContentLoadingProgressBar
import liang.lollipop.guidelinesview.util.*

/**
 * @author lollipop
 * @date 2019-11-23 12:18
 * 悬浮的View的辅助类
 */
class FloatingViewHelper private constructor(val anchorView: View,
                         private val floatingView: FloatingGroup) {

    companion object {
        fun create(anchorView: View, floatingView: View): FloatingViewHelper {
            val floatingGroup = FloatingGroup(anchorView.context)
            floatingGroup.addView(floatingView)
            return FloatingViewHelper(anchorView, floatingGroup)
        }

        fun create(anchorView: View, floatingId: Int): FloatingViewHelper {
            val floatingGroup = FloatingGroup(anchorView.context)
            LayoutInflater.from(anchorView.context)
                .inflate(floatingId, floatingGroup, true)
            return FloatingViewHelper(anchorView, floatingGroup)
        }
    }

    private var lastAnimationType: AnimationType = AnimationType.CircularReveal

    private var onShowListener: (() -> Unit)? = null

    var interpolator: Interpolator = AccelerateDecelerateInterpolator()

    var autoClose = true

    val contentView: View
        get() {
            return floatingView.getChildAt(0)
        }

    fun <T: View> findFromContent(id: Int): T? {
        return contentView.findViewById(id)
    }

    fun onShow(lis: (() -> Unit)?) {
        onShowListener = lis
    }

    fun showNearby(animationType: AnimationType = AnimationType.CircularReveal) {
        showOnAnchorParent(animationType, true)
    }

    fun showOutermost(animationType: AnimationType = AnimationType.CircularReveal) {
        showOnAnchorParent(animationType, false)
    }

    private fun showOnAnchorParent(animationType: AnimationType, isNearby: Boolean) {
        val viewGroup = findGroup(isNearby)?:throw RuntimeException("anchorView need Attach to Group")
        showToGroup(viewGroup, animationType)
    }

    fun showToTarget(targetId: Int, animationType: AnimationType = AnimationType.CircularReveal) {
        val rootGroup = findGroup(isNearby = false,
            isFilter = false)?:throw RuntimeException("anchorView need Attach to Group")
        val viewGroup = rootGroup.findViewById<ViewGroup>(targetId)?:
                throw RuntimeException("target group not found")
        showToGroup(viewGroup, animationType)
    }

    fun showToTarget(target: ViewGroup, animationType: AnimationType = AnimationType.CircularReveal) {
        showToGroup(target, animationType)
    }

    private fun showToGroup(viewGroup: ViewGroup, animationType: AnimationType) {
        removeFromGroup()
        floatingView.isAutoClose = autoClose
        floatingView.onCallBack {
            close()
        }
        floatingView.onAttached {
            animationIn(animationType)
        }
        floatingView.hide()
        viewGroup.addView(floatingView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun removeFromGroup() {
        floatingView.parent?.let { viewParent ->
            if (viewParent is ViewGroup) {
                viewParent.removeView(floatingView)
            }
        }
    }

    fun close(animationType: AnimationType? = null) {
        animationOut(animationType?:lastAnimationType)
    }

    private fun animationIn(animationType: AnimationType) {
        lastAnimationType = animationType
        onShowListener?.invoke()
        when (animationType) {
            AnimationType.CircularReveal -> {
                floatingView.revealOpenWith(anchorView) {
                    onStart {
                        floatingView.show()
                        removeThis(it)
                    }
                }
            }
            AnimationType.Left, AnimationType.Right -> {
                val direction = if (animationType == AnimationType.Left) {
                    -1F
                } else {
                    1F
                }
                floatingView.translationX = floatingView.width * direction
                floatingView.animate().let {
                    it.cancel()
                    it.translationX(0F)
                    it.interpolator = interpolator
                    it.lifecycleBinding {
                        onStart {
                            floatingView.show()
                            removeThis(it)
                        }
                    }
                    it.start()
                }
            }
            AnimationType.Top, AnimationType.Bottom -> {
                val direction = if (animationType == AnimationType.Top) {
                    -1F
                } else {
                    1F
                }
                floatingView.translationY = floatingView.height * direction
                floatingView.animate().let {
                    it.cancel()
                    it.translationY(0F)
                    it.interpolator = interpolator
                    it.lifecycleBinding {
                        onStart {
                            floatingView.show()
                            removeThis(it)
                        }
                    }
                    it.start()
                }
            }
            else -> {
                return
            }
        }
    }

    private fun animationOut(animationType: AnimationType) {
        lastAnimationType = animationType
        when (animationType) {
            AnimationType.CircularReveal -> {
                floatingView.revealCloseWith(anchorView) {
                    onEnd {
                        removeFromGroup()
                        removeThis(it)
                    }
                    onCancel {
                        removeThis(it)
                    }
                }
            }
            AnimationType.Left, AnimationType.Right -> {
                val direction = if (animationType == AnimationType.Left) {
                    -1F
                } else {
                    1F
                }
                floatingView.animate().let {
                    it.cancel()
                    it.translationX(floatingView.width * direction)
                    it.interpolator = interpolator
                    bindLifecycleForClose(it)
                    it.start()
                }
            }
            AnimationType.Top, AnimationType.Bottom -> {
                val direction = if (animationType == AnimationType.Top) {
                    -1F
                } else {
                    1F
                }
                floatingView.animate().let {
                    it.cancel()
                    it.translationY(floatingView.height * direction)
                    it.interpolator = interpolator
                    bindLifecycleForClose(it)
                    it.start()
                }
            }
            else -> {
                return
            }
        }
    }

    private fun bindLifecycleForClose(animator: ViewPropertyAnimator) {
        animator.lifecycleBinding {
            onEnd {
                removeFromGroup()
                removeThis(it)
            }
            onCancel {
                removeThis(it)
            }
        }
    }

    private fun findGroup(isNearby: Boolean, isFilter: Boolean = true): ViewGroup? {
        var group: ViewGroup? = null
        var view = anchorView
        do {
            val parent = view.parent ?: break
            // 只选择可以层叠显示的Layout，因此LinearLayout不可以
            if (!isFilter || parent is FrameLayout || parent is CoordinatorLayout ||
                    parent is ConstraintLayout || parent is RelativeLayout) {
                group = parent as ViewGroup
                if (isNearby) {
                    break
                }
            }
            if (parent is View) {
                view = parent
            } else {
                break
            }
        } while (true)

        return group
    }

    enum class AnimationType {
        None,
        CircularReveal,
        Left,
        Right,
        Top,
        Bottom
    }

    private class FloatingGroup(context: Context): FrameLayout(context) {

        var isAutoClose = true

        var isOnce = true

        private var onCloseListener: (() -> Unit)? = null

        private var onAttachedListener: (() -> Unit)? = null

        private var onDetachedListener: (() -> Unit)? = null

        override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
            if (isAutoClose && onCloseListener != null && keyCode == KeyEvent.KEYCODE_BACK) {
                onCloseListener?.invoke()
                return true
            }
            return true//super.onKeyDown(keyCode, event)
        }

        fun onAttached(lis: (() -> Unit)?) {
            onAttachedListener = lis
        }

        fun onDetached(lis: (() -> Unit)?) {
            onDetachedListener = lis
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            post {
                bindFocus()
                onAttachedListener?.invoke()
                if (isOnce) {
                    onAttachedListener = null
                }
            }
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            post {
                onDetachedListener?.invoke()
                if (isOnce) {
                    onDetachedListener = null
                }
            }
        }

        fun onCallBack(lis: (() -> Unit)?) {
            onCloseListener = lis
        }

        fun hide() {
            visibility = View.INVISIBLE
        }

        fun show() {
            visibility = View.VISIBLE
            bindFocus()
        }

        fun bindFocus() {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }

    }

}