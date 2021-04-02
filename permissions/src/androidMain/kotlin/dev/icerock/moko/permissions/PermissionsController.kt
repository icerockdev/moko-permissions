/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.coroutines.suspendCoroutine

actual class PermissionsController(
    val resolverFragmentTag: String = "PermissionsControllerResolver",
    val applicationContext: Context
) {
    var fragmentManager: FragmentManager? = null

    fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager

        val observer = object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed(source: LifecycleOwner) {
                this@PermissionsController.fragmentManager = null
                source.lifecycle.removeObserver(this)
            }
        }
        lifecycle.addObserver(observer)
    }

    actual suspend fun providePermission(permission: Permission) {
        val fragmentManager =
            fragmentManager
                ?: throw IllegalStateException("can't resolve permission without active window")

        val currentFragment: Fragment? = fragmentManager.findFragmentByTag(resolverFragmentTag)
        val resolverFragment: ResolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, resolverFragmentTag)
                    .commitNow()
            }
        }

        val platformPermission = permission.toPlatformPermission()
        suspendCoroutine<Unit> { continuation ->
            resolverFragment.requestPermission(
                permission,
                platformPermission
            ) { continuation.resumeWith(it) }
        }
    }

    actual fun isPermissionGranted(permission: Permission): Boolean {
        if (permission == Permission.REMOTE_NOTIFICATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()
        }
        return permission.toPlatformPermission().all {
            ContextCompat.checkSelfPermission(applicationContext, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    private fun Permission.toPlatformPermission(): List<String> {
        return when (this) {
            Permission.CAMERA -> listOf(Manifest.permission.CAMERA)
            Permission.GALLERY -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            Permission.STORAGE -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            Permission.WRITE_STORAGE -> listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Permission.LOCATION -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)
            Permission.COARSE_LOCATION -> listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            Permission.BLUETOOTH_LE -> listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            Permission.REMOTE_NOTIFICATION -> emptyList()
            Permission.RECORD_AUDIO -> listOf(Manifest.permission.RECORD_AUDIO)
        }
    }

    class ResolverFragment : Fragment() {
        init {
            retainInstance = true
        }

        private val permissionCallbackMap = mutableMapOf<Int, PermissionCallback>()

        fun requestPermission(
            permission: Permission,
            permissions: List<String>,
            callback: (Result<Unit>) -> Unit
        ) {
            val context = requireContext()
            val toRequest = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (toRequest.isEmpty()) {
                callback.invoke(Result.success(Unit))
                return
            }

            val requestCode = (permissionCallbackMap.keys.max() ?: 0) + 1
            permissionCallbackMap[requestCode] = PermissionCallback(permission, callback)

            requestPermissions(toRequest.toTypedArray(), requestCode)
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            val permissionCallback = permissionCallbackMap[requestCode] ?: return
            permissionCallbackMap.remove(requestCode)

            val isCancelled = grantResults.isEmpty()
            val success = !isCancelled && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (success) {
                permissionCallback.callback.invoke(Result.success(Unit))
            } else {
                if (shouldShowRequestPermissionRationale(permissions.first())) {
                    permissionCallback.callback.invoke(
                        Result.failure(
                            DeniedException(
                                permissionCallback.permission
                            )
                        )
                    )
                } else {
                    permissionCallback.callback.invoke(
                        Result.failure(
                            DeniedAlwaysException(
                                permissionCallback.permission
                            )
                        )
                    )
                }
            }
        }

        private class PermissionCallback(
            val permission: Permission,
            val callback: (Result<Unit>) -> Unit
        )
    }
}
