package liang.lollipop.electronicclock.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 异步辅助类
 * @author Lollipop
 */
object AsyncHelper {

    var corePoolSize = 5
    var maximumPoolSize = 50

    private val uiHandler = Handler(Looper.getMainLooper())

    private val executorService: ExecutorService by lazy {
        ThreadPoolExecutor(corePoolSize, maximumPoolSize,
            0L, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>())
    }

    fun execute(runnable: Runnable) {
        executorService.execute(runnable)
    }

    fun execute(run: () -> Unit) {
        executorService.execute(run)
    }

    fun runOnUIThread(runnable: Runnable) {
        uiHandler.post(runnable)
    }

    fun runOnUIThread(run: () -> Unit) {
        uiHandler.post(run)
    }

}

fun doAsync(onError:((e: Exception) -> Unit)? = null, run: () -> Unit) {
    AsyncHelper.execute {
        try {
            run()
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }
//    launch(Dispatchers.Main) {
//
//    }
}

fun uiThread(run: () -> Unit) {
    AsyncHelper.runOnUIThread(run)
}