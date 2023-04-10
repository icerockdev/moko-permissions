/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.test

import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController

expect abstract class PermissionsControllerMock constructor() : PermissionsController {
    abstract override suspend fun providePermission(permission: Permission)

    abstract override suspend fun isPermissionGranted(permission: Permission): Boolean

    companion object
}

fun createPermissionControllerMock(
    allow: Set<Permission> = emptySet(),
    granted: Set<Permission> = emptySet()
): PermissionsControllerMock = object : PermissionsControllerMock() {
    private val granted = mutableSetOf<Permission>().apply { addAll(granted) }

    override suspend fun providePermission(permission: Permission) {
        if (allow.contains(permission)) {
            this.granted.add(permission)
            return
        }
        if (this.granted.contains(permission)) return

        throw DeniedException(permission, "mock block permission")
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return this.granted.contains(permission)
    }

    override fun openAppSettings() = Unit

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return if (isPermissionGranted(permission)) PermissionState.Granted
        else PermissionState.NotDetermined
    }
}
