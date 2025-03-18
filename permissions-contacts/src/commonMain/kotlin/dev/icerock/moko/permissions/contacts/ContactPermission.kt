/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.contacts

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val contactsDelegate: PermissionDelegate
internal expect val readContactsDelegate: PermissionDelegate

/**
 * Permission to read and write contacts.
 *
 * On Android, declare both `READ_CONTACTS` and `WRITE_CONTACTS` permissions
 * in `AndroidManifest.xml`
 */
object ContactPermission : Permission {
    override val delegate get() = contactsDelegate
}

/**
 * Permission to read contacts
 *
 * On Android, declare `READ_CONTACTS` permission in `AndroidManifest.xml`
 *
 * On iOS this permission is the same with [ContactPermission]
 */
object ReadContactPermission : Permission {
    override val delegate get() = readContactsDelegate
}

val Permission.Companion.CONTACTS get() = ContactPermission
val Permission.Companion.READ_CONTACTS get() = ReadContactPermission