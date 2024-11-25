package dev.icerock.moko.permissions.location

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val locationDelegate: PermissionDelegate
internal expect val coarseLocationDelegate: PermissionDelegate
internal expect val backgroundLocationDelegate: PermissionDelegate

object LocationPermission : Permission {
    override val delegate get() = locationDelegate
}
object CoarseLocationPermission : Permission {
    override val delegate get() = coarseLocationDelegate
}
object BackgroundLocationPermission : Permission {
    override val delegate get() = backgroundLocationDelegate
}

val Permission.Companion.LOCATION get() = LocationPermission
val Permission.Companion.COARSE_LOCATION get() = CoarseLocationPermission
val Permission.Companion.BACKGROUND_LOCATION get() = BackgroundLocationPermission
