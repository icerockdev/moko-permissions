/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.Permission

interface PermissionsControllerProtocol {
    suspend fun providePermission(permission: Permission)
    fun isPermissionGranted(permission: Permission): Boolean
}
