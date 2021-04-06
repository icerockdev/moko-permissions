/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle

actual interface PermissionsController {
    actual suspend fun providePermission(permission: Permission)
    actual fun isPermissionGranted(permission: Permission): Boolean

    fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager)

    companion object {
        operator fun invoke(
            resolverFragmentTag: String = "PermissionsControllerResolver",
            applicationContext: Context
        ): PermissionsController {
            return PermissionsControllerImpl(
                resolverFragmentTag = resolverFragmentTag,
                applicationContext = applicationContext
            )
        }
    }
}
