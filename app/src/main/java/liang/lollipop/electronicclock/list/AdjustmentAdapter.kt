package liang.lollipop.electronicclock.list

import android.content.Intent
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.electronicclock.bean.*
import java.lang.ref.WeakReference

/**
 * @author lollipop
 * @date 2019-09-01 20:21
 */
class AdjustmentAdapter(private val data: ArrayList<AdjustmentInfo>,
                        private val inflater: LayoutInflater,
                        private val onValueChange: (info: AdjustmentInfo, newValue: Any) -> Unit,
                        private val requestActivity: (intent: Intent, requestCode: Int) -> Unit): RecyclerView.Adapter<AdjustmentHolder<*>>(),
    AdjustmentHolder.OnValueChangeListener {

    companion object {
        private const val TYPE_SWITCH = 0
        private const val TYPE_SEEKBAR = 1
        private const val TYPE_COLOR = 2
        private const val TYPE_PADDING = 3
        private const val TYPE_LIST = 4
        private const val TYPE_PHOTOS = 5
    }

    /**
     * 发起请求的holder的集合
     */
    private val requestMap = SparseArray<RequestHolder>()

    /**
     * 返回值的集合
     */
    private val resultMap = SparseArray<PendingResult>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdjustmentHolder<*> {
        val holder =  when (viewType) {
            TYPE_SWITCH -> AdjustmentBooleanHolder.create(inflater, parent)
            TYPE_SEEKBAR -> AdjustmentSeekBarHolder.create(inflater, parent)
            TYPE_COLOR -> AdjustmentColorHolder.create(inflater, parent)
            TYPE_PADDING -> AdjustmentPaddingHolder.create(inflater, parent)
            TYPE_LIST -> AdjustmentSelectHolder.create(inflater, parent)
            else -> throw RuntimeException("unknown the viewType:$viewType")
        }
        holder.onValueChangeListener = this
        if (holder is AdjustmentRequestActivityHolder) {
            holder.bindRequestActivityCallback { requestHolder, intent, i ->
                requestActivity(requestHolder, intent, i)
            }
        }
        return holder
    }

    private fun requestActivity(requestHolder: AdjustmentRequestActivityHolder,
                                intent: Intent, requestCode: Int) {
        if (requestHolder is AdjustmentHolder<*>) {
            val position = requestHolder.adapterPosition
            requestMap.put(requestCode, RequestHolder(position, getItemViewType(position), WeakReference(requestHolder)))
        }
        requestActivity(intent, requestCode)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        // 没有请求过，那么放弃事件
        val requestHolder = requestMap[requestCode] ?: return false
        // 如果item的类型变了，那么放弃事件
        if (getItemViewType(requestHolder.position) != requestHolder.itemType) {
            requestMap.remove(requestCode)
            return false
        }
        val holder = requestHolder.holder.get()
        // 如果holder被回收，或者被复用了，那么延迟holder
        if (holder == null ||
            (holder as AdjustmentHolder<*>).adapterPosition != requestHolder.position) {
            resultMap.put(requestCode, PendingResult(requestCode, resultCode, data))
            return true
        }
        holder.onActivityResult(requestCode, resultCode, data)
        requestMap.remove(requestCode)
        return true
    }

    override fun onValueChange(holder: AdjustmentHolder<*>, newValue: Any) {
        onValueChange(data[holder.adapterPosition], newValue)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is AdjustmentBoolean -> TYPE_SWITCH
            is AdjustmentInteger -> TYPE_SEEKBAR
            is AdjustmentColor -> TYPE_COLOR
            is AdjustmentPadding -> TYPE_PADDING
            is AdjustmentSelect -> TYPE_LIST
            is AdjustmentImages -> TYPE_PHOTOS
            else -> throw RuntimeException("unknown the AdjustmentInfo type")
        }
    }

    override fun onBindViewHolder(holder: AdjustmentHolder<*>, position: Int) {
        val info = data[position]
        when(holder) {
            is AdjustmentBooleanHolder -> if (info is AdjustmentBoolean) {
                holder.onBind(info)
            }
            is AdjustmentSeekBarHolder -> if (info is AdjustmentInteger) {
                holder.onBind(info)
            }
            is AdjustmentColorHolder -> if (info is AdjustmentColor) {
                holder.onBind(info)
            }
            is AdjustmentPaddingHolder -> if (info is AdjustmentPadding) {
                holder.onBind(info)
            }
            is AdjustmentSelectHolder -> if (info is AdjustmentSelect) {
                holder.onBind(info)
            }
        }
        tryResultToHolder(holder)
    }

    private fun tryResultToHolder(holder: AdjustmentHolder<*>) {
        // 如果它不包含身份，那么不可能发起请求
        if (holder is AdjustmentRequestActivityHolder) {
            val position = holder.adapterPosition
            // 检查它是否发起过请求
            for (i in 0 until requestMap.size()) {
                val key = requestMap.keyAt(i)
                val value = requestMap.valueAt(i)
                // 如果序号不一致，那么说明不是同一个
                if (value.position != position) {
                    continue
                }
                // 如果序号一致，item类型不一致，那么说明这个请求被放弃了
                // 移除这个请求
                if (value.itemType != getItemViewType(position)) {
                    requestMap.remove(key)
                    resultMap.remove(key)
                    return
                }
                val pendingResult = resultMap[key]
                // 还没有返回就绑定新的Holder并且 return
                if (pendingResult == null) {
                    value.holder = WeakReference(holder)
                    return
                }
                holder.onActivityResult(pendingResult.requestCode,
                    pendingResult.resultCode, pendingResult.data)
                // 使用过则移除
                resultMap.remove(key)
                requestMap.remove(key)
                return
            }
        }
    }

    private data class RequestHolder(val position: Int, val itemType: Int, var holder: WeakReference<AdjustmentRequestActivityHolder>)

    private data class PendingResult(val requestCode: Int, val resultCode: Int, val data: Intent?)

}