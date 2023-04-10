/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.test

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController

actual abstract class PermissionsControllerMock : PermissionsController {
    actual abstract override suspend fun providePermission(permission: Permission)

    actual abstract override suspend fun isPermissionGranted(permission: Permission): Boolean

    actual companion object
}
