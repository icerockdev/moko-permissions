/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.contacts

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionDelegate
import platform.Contacts.CNAuthorizationStatus
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNAuthorizationStatusDenied
import platform.Contacts.CNAuthorizationStatusNotDetermined
import platform.Contacts.CNAuthorizationStatusRestricted
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private class ContactsPermissionDelegate(
    private val permission: Permission,
) : PermissionDelegate {
    private val contactStore = CNContactStore()

    override suspend fun providePermission() {
        return provideLocationPermission(
            CNContactStore.authorizationStatusForEntityType(
                CNEntityType.CNEntityTypeContacts
            )
        )
    }

    override suspend fun getPermissionState(): PermissionState {
        val status: CNAuthorizationStatus =
            CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)
        return when (status) {
            CNAuthorizationStatusAuthorized -> PermissionState.Granted

            CNAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
            CNAuthorizationStatusDenied, CNAuthorizationStatusRestricted -> PermissionState.DeniedAlways
            else -> error("unknown contacts $status")
        }
    }

    private suspend fun provideLocationPermission(
        status: CNAuthorizationStatus
    ) {
        when (status) {
            CNAuthorizationStatusAuthorized,
            CNAuthorizationStatusRestricted -> return

            CNAuthorizationStatusNotDetermined -> {
                //  用户未选择权限，发起权限申请
                val newStatus = suspendCoroutine<CNAuthorizationStatus> { continuation ->
                    contactStore.requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) { flag, error ->
                        continuation.resume(
                            CNContactStore.authorizationStatusForEntityType(
                                CNEntityType.CNEntityTypeContacts
                            )
                        )
                    }
                }
                provideLocationPermission(newStatus)
            }

            CNAuthorizationStatusDenied -> throw DeniedAlwaysException(permission)
            else -> error("unknown location authorization status $status")
        }
    }
}

actual val contactsDelegate: PermissionDelegate = ContactsPermissionDelegate(ContactPermission)
