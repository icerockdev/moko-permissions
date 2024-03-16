package com.icerockdev

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.icerockdev.library.SampleViewModel
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import dev.icerock.moko.mvvm.getViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.background(Color.White)) {
                    TestScreen(
                        viewModel = getViewModel {
                            SampleViewModel(
                                eventsDispatcher = eventsDispatcherOnMain(),
                                permissionsController = PermissionsController(
                                    applicationContext = applicationContext
                                ),
                                permissionType = Permission.INSTALL_APPLICATION
                            )
                        }
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
fun TestScreen(viewModel: SampleViewModel) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val eventsListener = remember {
        object : SampleViewModel.EventListener {
            override fun onSuccess() {
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Permission successfully granted!"
                    )
                }
            }

            override fun onDenied(exception: DeniedException) {
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Permission denied!"
                    )
                }
            }

            override fun onDeniedAlways(exception: DeniedAlwaysException) {
                coroutineScope.launch {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Permission is always denied",
                        duration = SnackbarDuration.Long,
                        actionLabel = "Settings"
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.permissionsController.openAppSettings()
                    }
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(true) {
        viewModel.eventsDispatcher.bind(lifecycleOwner, eventsListener)
    }

    BindEffect(viewModel.permissionsController)

    Scaffold(scaffoldState = scaffoldState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = viewModel::onRequestPermissionButtonPressed,
                content = { Text("Request permission") }
            )
        }
    }
}
