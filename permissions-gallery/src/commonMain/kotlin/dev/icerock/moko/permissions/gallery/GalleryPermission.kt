/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.gallery

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val galleryDelegate: PermissionDelegate

object GalleryPermission : Permission {
    override val delegate get() = galleryDelegate
}

val Permission.Companion.GALLERY get() = GalleryPermission
