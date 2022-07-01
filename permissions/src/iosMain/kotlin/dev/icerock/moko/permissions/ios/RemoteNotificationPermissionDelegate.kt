/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.ios

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.mainContinuation
import platform.UIKit.UIApplication
import platform.UIKit.registeredForRemoteNotifications
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNNotificationSettings
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.suspendCoroutine

internal class RemoteNotificationPermissionDelegate : PermissionDelegate {
    override suspend fun providePermission() {
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
                            UNAuthorizationOptionSound
                                .or(UNAuthorizationOptionAlert)
                                .or(UNAuthorizationOptionBadge),
                            mainContinuation { isOk, error ->
                                if (isOk && error == null) {
                                    continuation.resumeWith(Result.success(true))
                                } else {
                                    continuation.resumeWith(Result.success(false))
                                }
                            }
                        )
                }
                if (isSuccess) {
                    providePermission()
                } else {
                    throw IllegalStateException("notifications permission failed")
                }
            }
            UNAuthorizationStatusDenied -> throw DeniedAlwaysException(Permission.REMOTE_NOTIFICATION)
            else -> throw IllegalStateException("notifications permission status $status")
        }
    }

    override fun isPermissionGranted(): Boolean {
        return UIApplication.sharedApplication().registeredForRemoteNotifications
    }

    override suspend fun getPermissionState(): PermissionState {
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
        return when (status) {
            UNAuthorizationStatusAuthorized -> PermissionState.Granted
            UNAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
            UNAuthorizationStatusDenied -> PermissionState.DeniedAlways
            else -> throw IllegalStateException("unknown push authorization status $status")
        }
    }
}
