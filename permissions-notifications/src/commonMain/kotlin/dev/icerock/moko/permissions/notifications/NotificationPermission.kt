package dev.icerock.moko.permissions.notifications

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val remoteNotificationDelegate: PermissionDelegate

object RemoteNotificationPermission : Permission {
    override val delegate get() = remoteNotificationDelegate
}

val Permission.Companion.REMOTE_NOTIFICATION get() = RemoteNotificationPermission
