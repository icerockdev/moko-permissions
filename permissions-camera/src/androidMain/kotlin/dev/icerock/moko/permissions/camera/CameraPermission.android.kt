/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.camera

import android.Manifest
import android.content.Context
import dev.icerock.moko.permissions.PermissionDelegate

actual val cameraDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context) = null
    override fun getPlatformPermission() = listOf(Manifest.permission.CAMERA)
}
