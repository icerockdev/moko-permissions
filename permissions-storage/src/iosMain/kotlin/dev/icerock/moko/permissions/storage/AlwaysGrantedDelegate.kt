/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.storage

import dev.icerock.moko.permissions.PermissionDelegate
import dev.icerock.moko.permissions.PermissionState

object AlwaysGrantedDelegate : PermissionDelegate {
    override suspend fun providePermission() = Unit
    override suspend fun getPermissionState() = PermissionState.Granted
}
