package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.PermissionState

internal class InstallApplicationPermissionDelegate : PermissionDelegate {
    override suspend fun providePermission() {
        // ignore
    }

    override suspend fun getPermissionState(): PermissionState = PermissionState.Granted
}