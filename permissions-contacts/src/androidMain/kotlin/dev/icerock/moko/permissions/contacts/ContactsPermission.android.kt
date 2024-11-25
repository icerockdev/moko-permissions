package dev.icerock.moko.permissions.contacts

import android.Manifest
import android.content.Context
import dev.icerock.moko.permissions.PermissionDelegate

actual val contactsDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context) = null
    
    override fun getPlatformPermission() =
        listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
}
