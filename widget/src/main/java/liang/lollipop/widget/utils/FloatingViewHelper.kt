package liang.lollipop.widget.utils

import android.content.Context
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
class FloatingViewHelper private constructor(private val anchorView: View,
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

    var interpolator: Interpolator = AccelerateDecelerateInterpolator()

    var autoClose = true

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
        floatingView.postOnAttached {
            animationIn(animationType)
        }
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
        when (animationType) {
            AnimationType.CircularReveal -> {
                floatingView.revealOpenWith(anchorView)
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

    private fun View.postOnAttached(run: () -> Unit) {
        this.addOnAttachStateChangeListener(ViewAttachStateChangeListener(true, run))
    }

    private fun View.postOnDetached(run: () -> Unit) {
        this.addOnAttachStateChangeListener(ViewAttachStateChangeListener(true, null, run))
    }

    private class ViewAttachStateChangeListener(
        private val ones: Boolean,
        private val onAttached: (() -> Unit)? = null,
        private val onDetached: (() -> Unit)? = null) : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            onAttached?.invoke()
            if (ones && onDetached == null) {
                v?.removeOnAttachStateChangeListener(this)
            }
        }

        override fun onViewAttachedToWindow(v: View?) {
            onDetached?.invoke()
            if (ones) {
                v?.removeOnAttachStateChangeListener(this)
            }
        }
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
        init {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }

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
            return super.onKeyDown(keyCode, event)
        }

        fun onCallBack(lis: (() -> Unit)?) {
            onCloseListener = lis
        }

    }

}