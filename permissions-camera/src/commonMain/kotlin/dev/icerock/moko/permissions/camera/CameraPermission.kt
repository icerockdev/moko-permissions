package dev.icerock.moko.permissions.camera

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val cameraDelegate: PermissionDelegate

object CameraPermission : Permission {
    override val delegate get() = cameraDelegate
}

val Permission.Companion.CAMERA get() = CameraPermission
