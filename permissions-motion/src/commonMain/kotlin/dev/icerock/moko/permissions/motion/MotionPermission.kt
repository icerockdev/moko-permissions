/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.motion

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val motionDelegate: PermissionDelegate

object MotionPermission : Permission {
    override val delegate get() = motionDelegate
}

val Permission.Companion.MOTION get() = MotionPermission
