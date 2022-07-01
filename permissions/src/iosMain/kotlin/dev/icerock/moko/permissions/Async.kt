/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import kotlinx.cinterop.staticCFunction
import platform.Foundation.NSThread
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync_f
import kotlin.native.concurrent.Continuation1
import kotlin.native.concurrent.Continuation2
import kotlin.native.concurrent.callContinuation1
import kotlin.native.concurrent.callContinuation2

internal inline fun <T1> mainContinuation(
    singleShot: Boolean = true,
    noinline block: (T1) -> Unit
) = Continuation1(
    block, staticCFunction { invokerArg ->
        if (NSThread.isMainThread()) {
            invokerArg!!.callContinuation1<T1>()
        } else {
            dispatch_sync_f(dispatch_get_main_queue(), invokerArg, staticCFunction { args ->
                args!!.callContinuation1<T1>()
            })
        }
    }, singleShot
)

internal inline fun <T1, T2> mainContinuation(
    singleShot: Boolean = true,
    noinline block: (T1, T2) -> Unit
) = Continuation2(
    block, staticCFunction { invokerArg ->
        if (NSThread.isMainThread()) {
            invokerArg!!.callContinuation2<T1, T2>()
        } else {
            dispatch_sync_f(dispatch_get_main_queue(), invokerArg, staticCFunction { args ->
                args!!.callContinuation2<T1, T2>()
            })
        }
    }, singleShot
)
