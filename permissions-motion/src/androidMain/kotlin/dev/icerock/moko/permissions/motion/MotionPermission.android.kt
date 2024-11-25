package dev.icerock.moko.permissions.motion

import android.Manifest
import android.content.Context
import android.os.Build
import dev.icerock.moko.permissions.PermissionDelegate

actual val motionDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context) = null
    
    override fun getPlatformPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.BODY_SENSORS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            listOf(Manifest.permission.BODY_SENSORS)
        } else {
            emptyList()
        }
}
