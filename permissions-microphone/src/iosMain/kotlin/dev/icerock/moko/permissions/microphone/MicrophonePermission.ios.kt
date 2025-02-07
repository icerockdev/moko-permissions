/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.microphone

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate
import dev.icerock.moko.permissions.avfoundation.AVCaptureDelegate
import platform.AVFoundation.AVMediaTypeAudio

actual val recordAudioDelegate: PermissionDelegate = AVCaptureDelegate(
    AVMediaTypeAudio,
    Permission.RECORD_AUDIO
)
