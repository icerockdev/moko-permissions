/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.notifications

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val remoteNotificationDelegate: PermissionDelegate

object RemoteNotificationPermission : Permission {
    override val delegate get() = remoteNotificationDelegate
}

val Permission.Companion.REMOTE_NOTIFICATION get() = RemoteNotificationPermission
