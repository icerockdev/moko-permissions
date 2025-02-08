/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.storage

import dev.icerock.moko.permissions.PermissionDelegate

actual val storageDelegate: PermissionDelegate = AlwaysGrantedDelegate
actual val writeStorageDelegate: PermissionDelegate = AlwaysGrantedDelegate
