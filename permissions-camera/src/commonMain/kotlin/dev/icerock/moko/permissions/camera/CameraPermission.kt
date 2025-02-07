/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.camera

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val cameraDelegate: PermissionDelegate

object CameraPermission : Permission {
    override val delegate get() = cameraDelegate
}

val Permission.Companion.CAMERA get() = CameraPermission
