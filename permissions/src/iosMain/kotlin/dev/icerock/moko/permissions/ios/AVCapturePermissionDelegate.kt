/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.mainContinuation
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaType
import platform.AVFoundation.requestAccessForMediaType

private fun AVCaptureDevice.Companion.requestAccess(
    type: AVMediaType,
    callback: (isGranted: Boolean) -> Unit
) {
    this.requestAccessForMediaType(type, mainContinuation { isGranted: Boolean ->
        callback(isGranted)
    })
}
