package dev.icerock.moko.permissions.notifications

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionDelegate

actual val remoteNotificationDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context): PermissionState? {
        if (Build.VERSION.SDK_INT !in VERSIONS_WITHOUT_NOTIFICATION_PERMISSION) return null

        val isNotificationsEnabled = NotificationManagerCompat.from(applicationContext)
            .areNotificationsEnabled()
        return if (isNotificationsEnabled) {
            PermissionState.Granted
        } else {
            PermissionState.DeniedAlways
        }
    }

    override fun getPlatformPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
}

private val VERSIONS_WITHOUT_NOTIFICATION_PERMISSION =
    Build.VERSION_CODES.KITKAT until Build.VERSION_CODES.TIRAMISU