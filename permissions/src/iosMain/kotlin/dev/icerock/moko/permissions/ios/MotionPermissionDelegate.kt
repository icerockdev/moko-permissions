package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.PermissionState
import platform.CoreMotion.CMAuthorizationStatusAuthorized
import platform.CoreMotion.CMAuthorizationStatusDenied
import platform.CoreMotion.CMAuthorizationStatusNotDetermined
import platform.CoreMotion.CMAuthorizationStatusRestricted
import platform.CoreMotion.CMMotionActivityManager
import platform.Foundation.NSOperationQueue

internal class MotionPermissionDelegate : PermissionDelegate {
    override suspend fun providePermission() {
        val manager = CMMotionActivityManager()
        manager.startActivityUpdatesToQueue(NSOperationQueue.mainQueue) {}
        manager.stopActivityUpdates()
    }

    @Suppress("MoveVariableDeclarationIntoWhen")
    override suspend fun getPermissionState(): PermissionState {
        val status = CMMotionActivityManager.authorizationStatus()
        return when (status) {
            CMAuthorizationStatusAuthorized,
            CMAuthorizationStatusRestricted -> PermissionState.Granted

            CMAuthorizationStatusDenied -> PermissionState.DeniedAlways
            CMAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
            else -> error("unknown motion authorization status $status")
        }
    }
}
