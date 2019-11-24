package liang.lollipop.electronicclock.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat

/**
 * @author lollipop
 * @date 2019-11-24 13:45
 * 渲染颜色的辅助类
 */
object TintUtil {

    fun tintText(view:TextView,vararg beans:TintBean){
        TintTextBuilder.with(view).add(*beans).tint()
    }

    fun tintWith(view: TextView): TintTextBuilder{
        return TintTextBuilder.with(view)
    }

    fun tintDrawable(drawable: Drawable): TintDrawableBuilder{
        return TintDrawableBuilder(drawable)
    }

    fun tintDrawable(context: Context,resId:Int): TintDrawableBuilder{
        return TintDrawableBuilder.whitResId(context,resId)
    }

    fun tintDrawable(context: Context,bitmap: Bitmap): TintDrawableBuilder{
        return TintDrawableBuilder.whitBitmap(context,bitmap)
    }

    class TintBean(val value:CharSequence,val color:Int){
        val length = value.length
    }

    class TintTextBuilder private constructor(private val view: TextView){

        private val tintBranArray = ArrayList<TintBean>()

        companion object {
            fun with(view: TextView):TintTextBuilder{
                return TintTextBuilder(view)
            }
        }

        fun add(vararg bean: TintBean): TintTextBuilder{
            tintBranArray.addAll(bean)
            return this
        }

        fun add(value:CharSequence,color: Int): TintTextBuilder{
            return add(TintBean(value,color))
        }

        fun add(value:String,color: Int): TintTextBuilder{
            return add(TintBean(value,color))
        }

        fun tint(){
            if(tintBranArray.isEmpty()){
                view.text = ""
                return
            }
            val strBuilder = StringBuilder()
            for(str in tintBranArray){
                strBuilder.append(str.value)
            }
            val spannable = SpannableStringBuilder(strBuilder.toString())
            var index = 0
            for(info in tintBranArray){
                if(info.length < 1){
                    continue
                }
                spannable.setSpan(ForegroundColorSpan(info.color),
                    index,index + info.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                index += info.length
            }
            view.text = spannable
        }

    }

    class TintDrawableBuilder(private val drawable: Drawable){

        private var colors: ColorStateList = ColorStateList.valueOf(Color.BLACK)

        companion object {

            fun whitResId(context: Context, resId:Int):TintDrawableBuilder{
                val wrappedDrawable = context.resources.getDrawable(resId, context.theme)
                return TintDrawableBuilder(wrappedDrawable)
            }

            fun whitBitmap(context: Context,bitmap: Bitmap):TintDrawableBuilder{
                val wrappedDrawable = BitmapDrawable(context.resources, bitmap)
                return TintDrawableBuilder(wrappedDrawable)
            }

        }

        fun mutate(): TintDrawableBuilder{
            drawable.mutate()
            return this
        }

        fun setColor(color: Int): TintDrawableBuilder{
            colors = ColorStateList.valueOf(color)
            return this
        }

        fun setColor(color: ColorStateList): TintDrawableBuilder{
            colors = color
            return this
        }

        fun tint(): Drawable{
            DrawableCompat.setTintList(drawable, colors)
            return drawable
        }

        fun into(view: ImageView) {
            view.setImageDrawable(tint())
        }

    }

}