package liang.lollipop.electronicclock.service

import android.app.WallpaperColors
import android.os.Bundle
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.WindowInsets

/**
 * 小部件的动态壁纸服务
 * @author Lollipop
 * @date 2019/11/26 21:51:34
 */
class WidgetWallpaper : WallpaperService() {

    private val widgetWallpaperEngine = object : Engine() {
        override fun onApplyWindowInsets(insets: WindowInsets?) {
            super.onApplyWindowInsets(insets)
        }

        override fun onComputeColors(): WallpaperColors? {
            return super.onComputeColors()
        }

        override fun onDesiredSizeChanged(desiredWidth: Int, desiredHeight: Int) {
            super.onDesiredSizeChanged(desiredWidth, desiredHeight)
        }

        override fun onDestroy() {
            super.onDestroy()
        }

        override fun onCommand(action: String?,
            x: Int, y: Int, z: Int,
            extras: Bundle?, resultRequested: Boolean): Bundle {
            return super.onCommand(action, x, y, z, extras, resultRequested)
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?, format: Int,
            width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onOffsetsChanged(xOffset: Float, yOffset: Float,
            xOffsetStep: Float, yOffsetStep: Float,
            xPixelOffset: Int, yPixelOffset: Int) {
            super.onOffsetsChanged(xOffset, yOffset,
                xOffsetStep, yOffsetStep,
                xPixelOffset, yPixelOffset)
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder?) {
            super.onSurfaceRedrawNeeded(holder)
        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
        }

        override fun setOffsetNotificationsEnabled(enabled: Boolean) {
            super.setOffsetNotificationsEnabled(enabled)
        }
    }

    override fun onCreateEngine(): Engine {
        return widgetWallpaperEngine
    }

}
