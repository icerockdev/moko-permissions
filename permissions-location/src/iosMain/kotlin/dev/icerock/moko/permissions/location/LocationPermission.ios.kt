/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.location

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionDelegate
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val locationManagerDelegate = LocationManagerDelegate()

private class LocationPermissionDelegate(
    private val permission: Permission
) : PermissionDelegate {
    override suspend fun providePermission() {
        return provideLocationPermission(CLLocationManager.authorizationStatus())
    }

    override suspend fun getPermissionState(): PermissionState {
        val status: CLAuthorizationStatus = CLLocationManager.authorizationStatus()
        return when (status) {
            kCLAuthorizationStatusAuthorizedAlways -> PermissionState.Granted
            kCLAuthorizationStatusAuthorizedWhenInUse -> {
                when (permission) {
                    is BackgroundLocationPermission -> PermissionState.NotGranted
                    else -> PermissionState.Granted
                }
            }

            kCLAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> PermissionState.DeniedAlways
            else -> error("unknown location authorization status $status")
        }
    }

    private suspend fun provideLocationPermission(
        status: CLAuthorizationStatus
    ) {
        when (status) {
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> return

            kCLAuthorizationStatusNotDetermined -> {
                val newStatus = suspendCoroutine<CLAuthorizationStatus> { continuation ->
                    locationManagerDelegate.requestLocationAccess { continuation.resume(it) }
                }
                provideLocationPermission(newStatus)
            }

            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> throw DeniedAlwaysException(permission)
            else -> error("unknown location authorization status $status")
        }
    }
}

actual val locationDelegate: PermissionDelegate =
    LocationPermissionDelegate(LocationPermission)
actual val coarseLocationDelegate: PermissionDelegate =
    LocationPermissionDelegate(CoarseLocationPermission)
actual val backgroundLocationDelegate: PermissionDelegate =
    LocationPermissionDelegate(BackgroundLocationPermission)
