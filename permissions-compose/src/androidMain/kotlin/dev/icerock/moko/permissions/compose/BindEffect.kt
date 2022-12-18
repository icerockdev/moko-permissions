/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.FragmentActivity
import dev.icerock.moko.permissions.PermissionsController

@Suppress("FunctionNaming")
@Composable
fun BindEffect(permissionsController: PermissionsController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val fragmentManager = (LocalContext.current as FragmentActivity).supportFragmentManager

    LaunchedEffect(true) {
        permissionsController.bind(lifecycleOwner.lifecycle, fragmentManager)
    }
}
