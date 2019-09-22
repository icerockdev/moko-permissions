package com.icerockdev.library

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val mainCoroutineContext: CoroutineContext = Dispatchers.Main
