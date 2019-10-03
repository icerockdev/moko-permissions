package com.icerockdev.library

import kotlinx.coroutines.*
import kotlin.coroutines.*

actual val mainCoroutineContext: CoroutineContext = MainLoopDispatcher

internal object MainLoopDispatcher: CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        val queue = dispatch_get_main_queue()
        dispatch_async(queue) {
            block.run()
        }
    }
}
