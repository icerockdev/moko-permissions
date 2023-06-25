/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.PermissionState
import platform.CoreMotion.CMAuthorizationStatusAuthorized
import platform.CoreMotion.CMAuthorizationStatusDenied
import platform.CoreMotion.CMAuthorizationStatusNotDetermined
import platform.CoreMotion.CMAuthorizationStatusRestricted
import platform.CoreMotion.CMMotionActivityManager
import platform.Foundation.NSDate
import platform.Foundation.NSOperationQueue

internal class MotionPermissionDelegate : PermissionDelegate {
    override suspend fun providePermission() {
        val cmActivityManager = CMMotionActivityManager()
        val now = NSDate()
        cmActivityManager.queryActivityStartingFromDate(
            now,
            now,
            NSOperationQueue.mainQueue()
        ) { _, _ -> }
    }

    @Suppress("MoveVariableDeclarationIntoWhen")
    override suspend fun getPermissionState(): PermissionState {
        val status = CMMotionActivityManager.authorizationStatus()
        return when (status) {
            CMAuthorizationStatusAuthorized,
            CMAuthorizationStatusRestricted,
            -> PermissionState.Granted

            CMAuthorizationStatusDenied -> PermissionState.DeniedAlways
            CMAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
            else -> error("unknown motion authorization status $status")
        }
    }
}
