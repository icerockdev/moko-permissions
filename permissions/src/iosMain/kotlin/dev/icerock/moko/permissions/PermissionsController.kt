/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import platform.AVFoundation.*
import platform.CoreLocation.*
import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class PermissionsController {
    private val locationManagerDelegate = LocationManagerDelegate()

    actual suspend fun providePermission(permission: Permission) {
        when (permission) {
            Permission.GALLERY -> provideGalleryPermission()
            Permission.CAMERA -> provideCameraPermission()
            Permission.STORAGE -> { } // not needed any permissions to storage
            Permission.LOCATION -> provideLocationPermition(permission)
            Permission.COARSE_LOCATION -> provideLocationPermition(permission)
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

    private suspend fun provideLocationPermition(permission: Permission, initialStatus: CLAuthorizationStatus? = null) {
        val status = initialStatus ?: CLLocationManager.authorizationStatus()
        when (status) {
            kCLAuthorizationStatusAuthorized,
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> return
            kCLAuthorizationStatusNotDetermined -> {
                val newStatus = suspendCoroutine<CLAuthorizationStatus> { continuation ->
                    locationManagerDelegate.requestLocationAccess { continuation.resume(it) }
                }
                provideLocationPermition(permission, newStatus)
            }
            kCLAuthorizationStatusDenied -> throw DeniedAlwaysException(permission)
            else -> throw IllegalStateException("location permission was denied")
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

internal class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    private var callback: ((CLAuthorizationStatus) -> Unit)? = null

    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    fun requestLocationAccess(callback: (CLAuthorizationStatus) -> Unit) {
        this.callback = callback

        locationManager.requestWhenInUseAuthorization()
    }

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        callback?.invoke(didChangeAuthorizationStatus)
        callback = null
    }
}