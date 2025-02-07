/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.camera

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate
import dev.icerock.moko.permissions.avfoundation.AVCaptureDelegate
import platform.AVFoundation.AVMediaTypeVideo

actual val cameraDelegate: PermissionDelegate = AVCaptureDelegate(
    AVMediaTypeVideo,
    Permission.CAMERA
)
