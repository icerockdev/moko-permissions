/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlin.coroutines.suspendCoroutine

class PermissionsControllerImpl(
    private val resolverFragmentTag: String = "PermissionsControllerResolver",
    private val applicationContext: Context
) : PermissionsController {
    private val fragmentManagerHolder = MutableStateFlow<FragmentManager?>(null)

    override fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
        this.fragmentManagerHolder.value = fragmentManager

        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed(source: LifecycleOwner) {
                this@PermissionsControllerImpl.fragmentManagerHolder.value = null
                source.lifecycle.removeObserver(this)
            }
        }
        lifecycle.addObserver(observer)
    }

    override suspend fun providePermission(permission: Permission) {
        val fragmentManager: FragmentManager = awaitFragmentManager()
        val resolverFragment: ResolverFragment = getOrCreateResolverFragment(fragmentManager)

        val platformPermission = permission.toPlatformPermission()
        suspendCoroutine<Unit> { continuation ->
            resolverFragment.requestPermission(
                permission,
                platformPermission
            ) { continuation.resumeWith(it) }
        }
    }

    override fun isPermissionGranted(permission: Permission): Boolean {
        if (permission == Permission.REMOTE_NOTIFICATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()
        }
        return permission.toPlatformPermission().all {
            val status = ContextCompat.checkSelfPermission(applicationContext, it)
            status == PackageManager.PERMISSION_GRANTED
        }
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        if (permission == Permission.REMOTE_NOTIFICATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val isNotificationsEnabled = NotificationManagerCompat.from(applicationContext)
                .areNotificationsEnabled()
            return if (isNotificationsEnabled) {
                PermissionState.Granted
            } else {
                PermissionState.DeniedAlways
            }
        }
        val permissions: List<String> = permission.toPlatformPermission()
        val status: List<Int> = permissions.map {
            ContextCompat.checkSelfPermission(applicationContext, it)
        }
        val isAllGranted: Boolean = status.all { it == PackageManager.PERMISSION_GRANTED }
        if (isAllGranted) return PermissionState.Granted

        val fragmentManager: FragmentManager = awaitFragmentManager()
        val resolverFragment: ResolverFragment = getOrCreateResolverFragment(fragmentManager)

        val isAllRequestRationale: Boolean = permissions.all {
            !resolverFragment.shouldShowRequestPermissionRationale(it)
        }
        return if (isAllRequestRationale) PermissionState.NotDetermined
        else PermissionState.Denied
    }

    override fun openAppSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", applicationContext.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(intent)
    }

    private suspend fun awaitFragmentManager(): FragmentManager {
        val fragmentManager: FragmentManager? = fragmentManagerHolder.value
        if (fragmentManager != null) return fragmentManager

        return fragmentManagerHolder.filterNotNull().first()
    }

    private fun getOrCreateResolverFragment(fragmentManager: FragmentManager): ResolverFragment {
        val currentFragment: Fragment? = fragmentManager.findFragmentByTag(resolverFragmentTag)
        return if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().also { fragment ->
                fragmentManager
                    .beginTransaction()
                    .add(fragment, resolverFragmentTag)
                    .commitNow()
            }
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
            Permission.REMOTE_NOTIFICATION -> emptyList()
            Permission.RECORD_AUDIO -> listOf(Manifest.permission.RECORD_AUDIO)
            Permission.BLUETOOTH_LE -> allBluetoothPermissions()
            Permission.BLUETOOTH_SCAN -> bluetoothScanCompat()
            Permission.BLUETOOTH_ADVERTISE -> bluetoothAdvertiseCompat()
            Permission.BLUETOOTH_CONNECT -> bluetoothConnectCompat()
        }
    }

    /**
     * @see https://developer.android.com/guide/topics/connectivity/bluetooth/permissions
     */
    private fun allBluetoothPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    private fun bluetoothScanCompat() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            listOf(Manifest.permission.BLUETOOTH)
        }

    private fun bluetoothAdvertiseCompat() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_ADVERTISE)
        } else {
            listOf(Manifest.permission.BLUETOOTH)
        }

    private fun bluetoothConnectCompat() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            listOf(Manifest.permission.BLUETOOTH)
        }
}
