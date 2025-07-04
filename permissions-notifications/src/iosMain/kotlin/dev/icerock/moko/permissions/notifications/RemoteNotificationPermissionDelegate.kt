/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.notifications

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.mainContinuation
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionCarPlay
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNNotificationSettings
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.suspendCoroutine

internal class RemoteNotificationPermissionDelegate : PermissionDelegate {
    override suspend fun providePermission() {
        return provideNotificationPermission(
            getPermissionStatus()
        )
    }

    override suspend fun getPermissionState(): PermissionState {
        val status: UNAuthorizationStatus = getPermissionStatus()

        return when (status) {
            UNAuthorizationStatusAuthorized,
            UNAuthorizationStatusProvisional,
            UNAuthorizationStatusEphemeral,
            -> PermissionState.Granted

            UNAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
            UNAuthorizationStatusDenied -> PermissionState.DeniedAlways
            else -> error("unknown push authorization status $status")
        }
    }

    private suspend fun getPermissionStatus(): UNAuthorizationStatus {
        val currentCenter = UNUserNotificationCenter.currentNotificationCenter()
        return suspendCoroutine { continuation ->
            currentCenter.getNotificationSettingsWithCompletionHandler(
                mainContinuation { settings: UNNotificationSettings? ->
                    continuation.resumeWith(
                        Result.success(
                            settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined
                        )
                    )
                }
            )
        }
    }

    private suspend fun provideNotificationPermission(
        status: UNAuthorizationStatus
    ) {
        when (status) {
            UNAuthorizationStatusAuthorized,
            UNAuthorizationStatusProvisional,
            UNAuthorizationStatusEphemeral -> return

            UNAuthorizationStatusNotDetermined -> {
                // User has not yet chosen permission, request permission
                val newStatus = suspendCoroutine<UNAuthorizationStatus> { continuation ->
                    UNUserNotificationCenter.currentNotificationCenter()
                        .requestAuthorizationWithOptions(
                            UNAuthorizationOptionSound
                                .or(UNAuthorizationOptionAlert)
                                .or(UNAuthorizationOptionBadge)
                                .or(UNAuthorizationOptionCarPlay),
                            mainContinuation { isOk, error ->
                                if (isOk && error == null) {
                                    continuation.resumeWith(Result.success(UNAuthorizationStatusAuthorized))
                                } else {
                                    continuation.resumeWith(Result.success(UNAuthorizationStatusDenied))
                                }
                            }
                        )
                }
                provideNotificationPermission(newStatus)
            }

            UNAuthorizationStatusDenied -> throw DeniedAlwaysException(Permission.REMOTE_NOTIFICATION)
            else -> error("unknown notifications authorization status $status")
        }
    }
}

actual val remoteNotificationDelegate: PermissionDelegate = RemoteNotificationPermissionDelegate()
