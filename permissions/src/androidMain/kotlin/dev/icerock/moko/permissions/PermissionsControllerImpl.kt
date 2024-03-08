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
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.suspendCoroutine

@Suppress("TooManyFunctions")
class PermissionsControllerImpl(
    private val resolverFragmentTag: String = "PermissionsControllerResolver",
    private val applicationContext: Context,
) : PermissionsController {
    private val fragmentManagerHolder = MutableStateFlow<FragmentManager?>(null)
    private val mutex: Mutex = Mutex()

    override fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
        this.fragmentManagerHolder.value = fragmentManager

        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    this@PermissionsControllerImpl.fragmentManagerHolder.value = null
                    source.lifecycle.removeObserver(this)
                }
            }
        }
        lifecycle.addObserver(observer)
    }

    override suspend fun providePermission(permission: Permission) {
        mutex.withLock {
            val fragmentManager: FragmentManager = awaitFragmentManager()
            val resolverFragment: ResolverFragment = getOrCreateResolverFragment(fragmentManager)

            val platformPermission = permission.toPlatformPermission()
            suspendCoroutine { continuation ->
                resolverFragment.requestPermission(
                    permission,
                    platformPermission
                ) { continuation.resumeWith(it) }
            }
        }
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return getPermissionState(permission) == PermissionState.Granted
    }

    @Suppress("ReturnCount")
    override suspend fun getPermissionState(permission: Permission): PermissionState {
        if (permission == Permission.REMOTE_NOTIFICATION &&
            Build.VERSION.SDK_INT in VERSIONS_WITHOUT_NOTIFICATION_PERMISSION
        ) {
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
            resolverFragment.shouldShowRequestPermissionRationale(it)
        }
        return if (isAllRequestRationale) PermissionState.Denied
        else PermissionState.NotGranted
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

        return withTimeoutOrNull(AWAIT_FRAGMENT_MANAGER_TIMEOUT_DURATION_MS) {
            fragmentManagerHolder.filterNotNull().first()
        } ?: error(
            "fragmentManager is null, `bind` function was never called," +
                " consider calling permissionsController.bind(lifecycle, fragmentManager)" +
                " or BindEffect(permissionsController) in the composable function," +
                " check the documentation for more info: " +
                    "https://github.com/icerockdev/moko-permissions/blob/master/README.md"
        )
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
                    .commit()
            }
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun Permission.toPlatformPermission(): List<String> {
        return when (this) {
            Permission.CAMERA -> listOf(Manifest.permission.CAMERA)
            Permission.GALLERY -> galleryCompat()
            Permission.STORAGE -> allStoragePermissions()
            Permission.WRITE_STORAGE -> listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Permission.LOCATION -> fineLocationCompat()
            Permission.COARSE_LOCATION -> listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            Permission.REMOTE_NOTIFICATION -> remoteNotificationsPermissions()
            Permission.RECORD_AUDIO -> listOf(Manifest.permission.RECORD_AUDIO)
            Permission.BLUETOOTH_LE -> allBluetoothPermissions()
            Permission.BLUETOOTH_SCAN -> bluetoothScanCompat()
            Permission.BLUETOOTH_ADVERTISE -> bluetoothAdvertiseCompat()
            Permission.BLUETOOTH_CONNECT -> bluetoothConnectCompat()
            Permission.CONTACTS-> listOf(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS)
            Permission.MOTION -> motionPermissions()
        }
    }

    /**
     * Behavior changes: Apps targeting Android 13 or higher
     *
     * @see https://developer.android.com/about/versions/13/behavior-changes-13#granular-media-permissions
     */

    private fun allStoragePermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    private fun galleryCompat() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    private fun fineLocationCompat() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    /**
     * Bluetooth permissions
     *
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

    private fun motionPermissions() =
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

    private fun remoteNotificationsPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }

    private companion object {
        val VERSIONS_WITHOUT_NOTIFICATION_PERMISSION =
            Build.VERSION_CODES.KITKAT until Build.VERSION_CODES.TIRAMISU
        private const val AWAIT_FRAGMENT_MANAGER_TIMEOUT_DURATION_MS = 2000L
    }
}
