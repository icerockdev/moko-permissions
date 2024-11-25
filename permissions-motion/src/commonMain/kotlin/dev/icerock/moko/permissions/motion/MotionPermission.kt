package dev.icerock.moko.permissions.motion

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val motionDelegate: PermissionDelegate

object MotionPermission : Permission {
    override val delegate get() = motionDelegate
}

val Permission.Companion.MOTION get() = MotionPermission
