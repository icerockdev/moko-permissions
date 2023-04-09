/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

internal class ResolverFragment : Fragment() {
    init {
        retainInstance = true
    }

    private val permissionCallbackMap = mutableMapOf<Int, PermissionCallback>()

    fun requestPermission(
        permission: Permission,
        permissions: List<String>,
        callback: (Result<Unit>) -> Unit
    ) {
        lifecycleScope.launchWhenCreated {
            val context = requireContext()
            val toRequest = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (toRequest.isEmpty()) {
                callback.invoke(Result.success(Unit))
                return@launchWhenCreated
            }

            val requestCode = (permissionCallbackMap.keys.maxOrNull() ?: 0) + 1
            permissionCallbackMap[requestCode] = PermissionCallback(permission, callback)

            requestPermissions(toRequest.toTypedArray(), requestCode)
        }
    }

    @Suppress("UnreachableCode")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionCallback = permissionCallbackMap[requestCode] ?: return
        permissionCallbackMap.remove(requestCode)

        managePermissions(permissionCallback, permissions, grantResults)
    }

    private fun managePermissions(
        permissionCallback: PermissionCallback,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val isCancelled = grantResults.isEmpty() || permissions.isEmpty()
        if (isCancelled) {
            permissionCallback.callback.invoke(
                Result.failure(RequestCanceledException(permissionCallback.permission))
            )
            return
        }
        val success = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        if (success) {
            permissionCallback.callback.invoke(Result.success(Unit))
        } else {
            if (shouldShowRequestPermissionRationale(permissions.first())) {
                permissionCallback.callback.invoke(
                    Result.failure(DeniedException(permissionCallback.permission))
                )
            } else {
                permissionCallback.callback.invoke(
                    Result.failure(DeniedAlwaysException(permissionCallback.permission))
                )
            }
        }
    }

    private class PermissionCallback(
        val permission: Permission,
        val callback: (Result<Unit>) -> Unit
    )
}
