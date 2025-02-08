/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.contacts

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val contactsDelegate: PermissionDelegate

object ContactPermission : Permission {
    override val delegate get() = contactsDelegate
}

val Permission.Companion.CONTACTS get() = ContactPermission
