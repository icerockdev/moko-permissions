/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.microphone

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val recordAudioDelegate: PermissionDelegate

object RecordAudioPermission : Permission {
    override val delegate get() = recordAudioDelegate
}

val Permission.Companion.RECORD_AUDIO get() = RecordAudioPermission
