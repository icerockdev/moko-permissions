package dev.icerock.moko.permissions.storage

import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionDelegate

object AlwaysGrantedDelegate : PermissionDelegate {
    override suspend fun providePermission() = Unit
    override suspend fun getPermissionState() = PermissionState.Granted
}
