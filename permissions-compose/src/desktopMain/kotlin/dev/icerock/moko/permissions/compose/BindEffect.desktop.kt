package dev.icerock.moko.permissions.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.icerock.moko.permissions.PermissionsController

@Composable
@Suppress("FunctionNaming")
actual fun BindEffect(permissionsController: PermissionsController) {
    LaunchedEffect(permissionsController) {
        permissionsController.bind()
    }
}