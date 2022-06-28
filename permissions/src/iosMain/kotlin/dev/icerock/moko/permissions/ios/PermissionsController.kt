/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.*
import platform.AVFoundation.*
import platform.CoreBluetooth.*
import platform.CoreLocation.*
import platform.Foundation.NSSelectorFromString
import platform.Photos.*
import platform.UIKit.UIApplication
import platform.UIKit.registeredForRemoteNotifications
import platform.UserNotifications.*
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PermissionsController : PermissionsControllerProtocol {
    private val locationManagerDelegate = LocationManagerDelegate()

    override suspend fun providePermission(permission: Permission) {
        when (permission) {
            Permission.GALLERY -> provideGalleryPermission()
            Permission.CAMERA -> provideCameraPermission()
            Permission.STORAGE -> Unit // no permissions required to use storage
            Permission.WRITE_STORAGE -> Unit // no permissions required to use storage
            Permission.LOCATION -> provideLocationPermission(permission)
            Permission.COARSE_LOCATION -> provideLocationPermission(permission)
            Permission.REMOTE_NOTIFICATION -> provideRemoteNotificationPermission()
            Permission.RECORD_AUDIO -> provideRecordAudioPermission()
            Permission.BLUETOOTH_LE,
            Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_ADVERTISE,
            Permission.BLUETOOTH_CONNECT -> provideBluetoothPermission(permission)
        }
    }

    override fun isPermissionGranted(permission: Permission): Boolean {
        return when (permission) {
            Permission.CAMERA -> AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
            Permission.GALLERY -> PHPhotoLibrary.authorizationStatus() == PHAuthorizationStatusAuthorized
            Permission.STORAGE -> true
            Permission.WRITE_STORAGE -> true
            Permission.LOCATION,
            Permission.COARSE_LOCATION -> {
                return listOf(
                    kCLAuthorizationStatusAuthorized,
                    kCLAuthorizationStatusAuthorizedAlways,
                    kCLAuthorizationStatusAuthorizedWhenInUse
                ).contains(CLLocationManager.authorizationStatus())
            }
            Permission.REMOTE_NOTIFICATION -> UIApplication.sharedApplication().registeredForRemoteNotifications
            Permission.RECORD_AUDIO -> AVCaptureDevice.authorizationStatusForMediaType(
                AVMediaTypeAudio
            ) == AVAuthorizationStatusAuthorized
            Permission.BLUETOOTH_LE,
            Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_ADVERTISE,
            Permission.BLUETOOTH_CONNECT -> isBluetoothAuthorized()
        }
    }

    private suspend fun provideRemoteNotificationPermission() {
        val currentCenter = UNUserNotificationCenter.currentNotificationCenter()

        val status = suspendCoroutine<UNAuthorizationStatus> { continuation ->
            currentCenter.getNotificationSettingsWithCompletionHandler(
                mainContinuation { settings: UNNotificationSettings? ->
                    continuation.resumeWith(
                        Result.success(
                            settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined
                        )
                    )
                })
        }
        when (status) {
            UNAuthorizationStatusAuthorized -> return
            UNAuthorizationStatusNotDetermined -> {
                val isSuccess = suspendCoroutine<Boolean> { continuation ->
                    UNUserNotificationCenter.currentNotificationCenter()
                        .requestAuthorizationWithOptions(
                            UNAuthorizationOptionSound.or(UNAuthorizationOptionAlert).or(
                                UNAuthorizationOptionBadge
                            ), mainContinuation { isOk, error ->
                                if (isOk == true && error == null) {
                                    continuation.resumeWith(Result.success(true))
                                } else {
                                    continuation.resumeWith(Result.success(false))
                                }
                            })
                }
                if (isSuccess) {
                    provideRemoteNotificationPermission()
                } else {
                    throw IllegalStateException("notifications permission failed")
                }
            }
            UNAuthorizationStatusDenied -> throw DeniedAlwaysException(Permission.REMOTE_NOTIFICATION)
            else -> throw IllegalStateException("notifications permission status $status")
        }
    }

    private suspend fun provideGalleryPermission(initialStatus: PHAuthorizationStatus? = null) {
        val status = initialStatus ?: PHPhotoLibrary.authorizationStatus()
        when (status) {
            PHAuthorizationStatusAuthorized -> return
            PHAuthorizationStatusNotDetermined -> {
                val newStatus = suspendCoroutine<PHAuthorizationStatus> { continuation ->
                    requestGalleryAccess { continuation.resume(it) }
                }
                provideGalleryPermission(newStatus)
            }
            PHAuthorizationStatusDenied -> throw DeniedAlwaysException(Permission.GALLERY)
            else -> throw IllegalStateException("gallery status $status")
        }
    }

    private suspend fun provideCameraPermission(initialStatus: AVAuthorizationStatus? = null) {
        val status =
            initialStatus ?: AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        when (status) {
            AVAuthorizationStatusAuthorized -> return
            AVAuthorizationStatusNotDetermined -> {
                suspendCoroutine<Unit> { continuation ->
                    requestCameraAccess { continuation.resume(Unit) }
                }
                provideCameraPermission()
            }
            AVAuthorizationStatusDenied -> throw DeniedAlwaysException(Permission.CAMERA)
            else -> throw IllegalStateException("camera status $status")
        }
    }

    private suspend fun provideRecordAudioPermission(initialStatus: AVAuthorizationStatus? = null) {
        val status =
            initialStatus ?: AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeAudio)
        when (status) {
            AVAuthorizationStatusAuthorized -> return
            AVAuthorizationStatusNotDetermined -> {
                suspendCoroutine<Unit> { continuation ->
                    requestRecordAudioAccess { continuation.resume(Unit) }
                }
                provideRecordAudioPermission()
            }
            AVAuthorizationStatusDenied -> throw DeniedAlwaysException(Permission.RECORD_AUDIO)
            else -> throw IllegalStateException("audio record status $status")
        }
    }

    private suspend fun provideLocationPermission(
        permission: Permission,
        initialStatus: CLAuthorizationStatus? = null
    ) {
        val status = initialStatus ?: CLLocationManager.authorizationStatus()
        when (status) {
            kCLAuthorizationStatusAuthorized,
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> return
            kCLAuthorizationStatusNotDetermined -> {
                val newStatus = suspendCoroutine<CLAuthorizationStatus> { continuation ->
                    locationManagerDelegate.requestLocationAccess { continuation.resume(it) }
                }
                provideLocationPermission(permission, newStatus)
            }
            kCLAuthorizationStatusDenied -> throw DeniedAlwaysException(permission)
            else -> throw IllegalStateException("location permission was denied")
        }
    }

    private fun isBluetoothAuthorized(): Boolean {
        // To maintain compatibility with iOS 12 (@see https://developer.apple.com/documentation/corebluetooth/cbmanagerauthorization)
        if (CBManager.resolveClassMethod(NSSelectorFromString("authorization"))) {
            return CBManager.authorization == CBManagerAuthorizationAllowedAlways
        }
        return CBCentralManager().state == CBManagerStatePoweredOn
    }

    private suspend fun provideBluetoothPermission(permission: Permission) {
        val isNotDetermined: Boolean
        // To maintain compatibility with iOS 12 (@see https://developer.apple.com/documentation/corebluetooth/cbmanagerauthorization)
        if (CBManager.resolveClassMethod(NSSelectorFromString("authorization"))) {
            isNotDetermined = CBManager.authorization == CBManagerAuthorizationNotDetermined
        } else {
            isNotDetermined = CBCentralManager().state == CBManagerStateUnknown
        }
        
        val state: CBManagerState
        if (isNotDetermined) {
            state = suspendCoroutine { continuation ->
                CBCentralManager(object : NSObject(), CBCentralManagerDelegateProtocol {
                    override fun centralManagerDidUpdateState(central: CBCentralManager) {
                        continuation.resume(central.state)
                    }
                }, null)
            }
        } else {
            state = CBCentralManager().state
        }

        when (state) {
            CBManagerStatePoweredOn -> return
            CBManagerStateUnauthorized -> throw DeniedAlwaysException(permission)
            CBManagerStatePoweredOff -> throw DeniedException(permission, "Bluetooth is powered off.")
            CBManagerStateResetting -> throw DeniedException(permission, "Bluetooth is restarting.")
            CBManagerStateUnsupported -> throw DeniedAlwaysException(permission, "Bluetooth is not supported on this device.")
            CBManagerStateUnknown -> throw IllegalStateException("Bluetooth state should be known at this point.")
            else -> throw IllegalStateException("Unknown state (Permissions library should be updated) : $state")
        }
    }
}

private fun requestGalleryAccess(callback: (PHAuthorizationStatus) -> Unit) {
    PHPhotoLibrary.requestAuthorization(mainContinuation { status: PHAuthorizationStatus ->
        callback(status)
    })
}

private fun requestCameraAccess(callback: () -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo, mainContinuation { _: Boolean ->
        callback()
    })
}

private fun requestRecordAudioAccess(callback: () -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeAudio, mainContinuation { _: Boolean ->
        callback()
    })
}
