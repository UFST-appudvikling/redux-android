package dk.ufst.arch

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*


interface Executor {
    fun execute(block: () -> Unit)
    fun runOnUiThread(block: () -> Unit)
}


@Suppress("unused")
class ThreadExecutor : Executor {
    private var executorService: ExecutorService = Executors.newFixedThreadPool(5)
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun execute(block: () -> Unit) {
        executorService.execute(block)
    }

    override fun runOnUiThread(block: () -> Unit) {
        handler.post { block() }
    }
}

/**
 * Singlethreaded executer used for testing
 */
class TestExecutor : Executor {
    override fun execute(block: () -> Unit) {
        block()
    }

    override fun runOnUiThread(block: () -> Unit) {
        block()
    }
}