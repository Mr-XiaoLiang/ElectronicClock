package liang.lollipop.electronicclock.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @author lollipop
 * @date 2019-11-04 22:51
 * 矩形的layout
 */
class SquareLayout(context: Context, attr: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    FrameLayout(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if(widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY){
            //如果已经固定了，那么不再修改
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        var newWidthMeasureSpec = widthMeasureSpec
        var newHeightMeasureSpec = heightMeasureSpec
        if(widthMode == MeasureSpec.EXACTLY){
            //如果只有宽度固定，那么调整高度
            val heightSize = MeasureSpec.getSize(widthMeasureSpec)
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY)
//            newWidthMeasureSpec = newHeightMeasureSpec
        }else if(heightMode == MeasureSpec.EXACTLY){
            //如果只有高度固定，那么调整宽度
            val widthSize = MeasureSpec.getSize(heightMeasureSpec)
            newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,MeasureSpec.EXACTLY)
        }
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }

}