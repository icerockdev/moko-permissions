package com.icerockdev

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.icerockdev.library.SampleViewModel
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import dev.icerock.moko.mvvm.getViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController

class MainActivity : AppCompatActivity(), SampleViewModel.EventListener {

    private lateinit var viewModel: SampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creates viewModel from common code.
        viewModel = getViewModel {
            SampleViewModel(
                eventsDispatcher = eventsDispatcherOnMain(),
                permissionsController = PermissionsController(applicationContext = applicationContext),
                permissionType = Permission.CONTACTS
            )
        }.also {
            it.permissionsController.bind(this)
            it.eventsDispatcher.bind(this, this)
        }
    }

    fun onRequestButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        // Starts permission providing process.
        viewModel.onRequestPermissionButtonPressed()
    }

    override fun onSuccess() {
        showToast("Permission successfully granted!")
    }

    override fun onDenied(exception: DeniedException) {
        showToast("Permission denied!")
    }

    override fun onDeniedAlways(exception: DeniedAlwaysException) {
        Snackbar
            .make(
                findViewById<LinearLayout>(R.id.root_view),
                "Permission is always denied",
                Snackbar.LENGTH_LONG
            )
            .setAction("Settings") {
                openAppSettings()
            }
            .show()
    }

    private fun openAppSettings() {
        viewModel.permissionsController.openAppSettings()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
