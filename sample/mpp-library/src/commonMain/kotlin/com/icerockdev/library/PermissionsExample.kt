package com.icerockdev.library

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

expect val mainCoroutineContext: CoroutineContext

class PermissionsExample(
    override val coroutineContext: CoroutineContext,
    private val permissionsController: PermissionsController
) : CoroutineScope {

    fun providePermission(permission: Permission, onResult: (Throwable?) -> Unit) {
        launch {
            try {
                // Calls suspend function in the coroutine to request some permission
                permissionsController.providePermission(permission)
                onResult(null)
            } catch (exception: Throwable) {
                onResult(exception)
            }
        }
    }

}
