package com.icerockdev.library

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.DeniedAlwaysException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

expect val mainCoroutineContext: CoroutineContext

class PermissionsHandler(
    override val coroutineContext: CoroutineContext,
    private val permissionsController: PermissionsController
) : CoroutineScope {

    /**
     * An example of using [PermissionsController] in common code.
     */
    fun providePermission(permission: Permission, listener: PermissionsProviderListener) {
        launch {
            try {
                // Calls suspend function in the coroutine to request some permission.
                permissionsController.providePermission(permission)
                // If there are no exceptions, permission has been granted successfully.
                listener.onSuccess()
            } catch (deniedAlwaysException: DeniedAlwaysException) {
                listener.onDeniedAlways(deniedAlwaysException)
            } catch (deniedException: DeniedException) {
                listener.onDenied(deniedException)
            }
        }
    }

    interface PermissionsProviderListener {

        fun onSuccess()

        fun onDenied(exception: DeniedException)

        fun onDeniedAlways(exception: DeniedAlwaysException)

    }
}
