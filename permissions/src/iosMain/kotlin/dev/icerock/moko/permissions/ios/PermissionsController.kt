/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.LocationManagerDelegate
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.Contacts.CNContactStore
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class PermissionsController : PermissionsControllerProtocol {
    private val locationManagerDelegate = LocationManagerDelegate()
    private val contactStore = CNContactStore()

    override suspend fun providePermission(permission: Permission) {
        return getDelegate(permission).providePermission()
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return getDelegate(permission).getPermissionState() == PermissionState.Granted
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return getDelegate(permission).getPermissionState()
    }

    override fun openAppSettings() {
        val settingsUrl: NSURL = NSURL.URLWithString(UIApplicationOpenSettingsURLString)!!
        UIApplication.sharedApplication.openURL(settingsUrl)
    }

    private fun getDelegate(permission: Permission): PermissionDelegate {
        return when (permission) {
            Permission.REMOTE_NOTIFICATION -> RemoteNotificationPermissionDelegate()
            Permission.CAMERA -> AVCapturePermissionDelegate(AVMediaTypeVideo, permission)
            Permission.GALLERY -> GalleryPermissionDelegate()
            Permission.STORAGE, Permission.WRITE_STORAGE -> AlwaysGrantedPermissionDelegate()
            Permission.LOCATION, Permission.COARSE_LOCATION, Permission.BACKGROUND_LOCATION ->
                LocationPermissionDelegate(locationManagerDelegate, permission)

            Permission.RECORD_AUDIO -> AVCapturePermissionDelegate(AVMediaTypeAudio, permission)
            Permission.BLUETOOTH_LE, Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_ADVERTISE, Permission.BLUETOOTH_CONNECT ->
                BluetoothPermissionDelegate(permission)

            Permission.CONTACTS->ContactsPermissionDelegate(permission,contactStore)

            Permission.MOTION -> MotionPermissionDelegate()
        }
    }
}
