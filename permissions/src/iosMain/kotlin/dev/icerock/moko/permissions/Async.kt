/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.Foundation.NSRunLoop
import platform.Foundation.NSThread
import platform.Foundation.performBlock
import kotlin.coroutines.CoroutineContext

/**
 * Simple object made to ensure dispatching to the main looper on iOS
 */
object MainRunDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) =
        NSRunLoop.mainRunLoop.performBlock { block.run() }
}

internal inline fun <T1> mainContinuation(
    noinline block: (T1) -> Unit
): (T1) -> Unit = { arg1 ->
    if (NSThread.isMainThread()) {
        block.invoke(arg1)
    } else {
        MainRunDispatcher.run {
            block.invoke(arg1)
        }
    }
}

internal inline fun <T1, T2> mainContinuation(
    noinline block: (T1, T2) -> Unit
): (T1, T2) -> Unit = { arg1, arg2 ->
    if (NSThread.isMainThread()) {
        block.invoke(arg1, arg2)
    } else {
        MainRunDispatcher.run {
            block.invoke(arg1, arg2)
        }
    }
}
