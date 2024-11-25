package dev.icerock.moko.permissions.storage

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val storageDelegate: PermissionDelegate
internal expect val writeStorageDelegate: PermissionDelegate

object StoragePermission : Permission {
    override val delegate get() = storageDelegate
}
object WriteStoragePermission : Permission {
    override val delegate get() = writeStorageDelegate
}

val Permission.Companion.STORAGE get() = StoragePermission
val Permission.Companion.WRITE_STORAGE get() = WriteStoragePermission
