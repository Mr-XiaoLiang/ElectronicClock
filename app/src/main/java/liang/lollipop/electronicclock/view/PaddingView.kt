package liang.lollipop.electronicclock.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * 调整Padding并预览效果的View
 * @author Lollipop
 */
class PaddingView(context: Context, attr: AttributeSet?,
                  defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)



}