/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.coroutines.suspendCoroutine

actual class PermissionsController(
    val resolverFragmentTag: String = "PermissionsControllerResolver"
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
            resolverFragment.requestPermission(platformPermission) { continuation.resumeWith(it) }
        }
    }

    private fun Permission.toPlatformPermission(): String {
        return when (this) {
            Permission.CAMERA -> Manifest.permission.CAMERA
            Permission.GALLERY -> Manifest.permission.READ_EXTERNAL_STORAGE
            Permission.STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
            Permission.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
            Permission.COARSE_LOCATION -> Manifest.permission.ACCESS_COARSE_LOCATION
        }
    }

    class ResolverFragment : Fragment() {
        init {
            retainInstance = true
        }

        private val codeCallbackMap = mutableMapOf<Int, (Result<Unit>) -> Unit>()

        fun requestPermission(permission: String, callback: (Result<Unit>) -> Unit) {
            val context = requireContext()
            if (ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                callback.invoke(Result.success(Unit))
                return
            }

            val requestCode = codeCallbackMap.keys.sorted().lastOrNull() ?: 0
            codeCallbackMap[requestCode] = callback

            requestPermissions(arrayOf(permission), requestCode)
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            val callback = codeCallbackMap[requestCode] ?: return
            codeCallbackMap.remove(requestCode)

            val success = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (success) {
                callback.invoke(Result.success(Unit))
            } else {
                if (shouldShowRequestPermissionRationale(permissions.first())) {
                    callback.invoke(Result.failure(DeniedException()))
                } else {
                    callback.invoke(Result.failure(DeniedNeverAskException()))
                }
            }
        }
    }

    class DeniedException : Throwable()
    class DeniedNeverAskException : Throwable()
}
