package dev.icerock.moko.permissions.storage

import dev.icerock.moko.permissions.PermissionDelegate

actual val storageDelegate: PermissionDelegate = AlwaysGrantedDelegate
actual val writeStorageDelegate: PermissionDelegate = AlwaysGrantedDelegate
