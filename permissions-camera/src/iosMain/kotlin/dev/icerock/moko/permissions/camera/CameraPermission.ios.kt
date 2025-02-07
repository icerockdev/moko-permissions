package dev.icerock.moko.permissions.camera

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate
import dev.icerock.moko.permissions.avfoundation.AVCaptureDelegate
import platform.AVFoundation.AVMediaTypeVideo

actual val cameraDelegate: PermissionDelegate = AVCaptureDelegate(
    AVMediaTypeVideo,
    Permission.CAMERA
)
