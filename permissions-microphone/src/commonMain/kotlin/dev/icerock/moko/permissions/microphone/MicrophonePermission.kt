package dev.icerock.moko.permissions.microphone

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionDelegate

internal expect val recordAudioDelegate: PermissionDelegate

object RecordAudioPermission : Permission {
    override val delegate get() = recordAudioDelegate
}

val Permission.Companion.RECORD_AUDIO get() = RecordAudioPermission
