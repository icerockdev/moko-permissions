package dev.icerock.moko.permissions

import android.content.Context

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface PermissionDelegate {
    fun getPermissionStateOverride(applicationContext: Context): PermissionState?
    fun getPlatformPermission(): List<String>
}
