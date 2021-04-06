package com.icerockdev

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
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
import dev.icerock.moko.permissions.PermissionsController

class MainActivity : AppCompatActivity(), SampleViewModel.EventListener {

    private lateinit var viewModel: SampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prepares the permissions controller and binds it to the activity lifecycle.
        val permissionsController = PermissionsController(applicationContext = this).also {
            it.bind(lifecycle, supportFragmentManager)
        }

        // Creates viewModel from common code.
        viewModel = getViewModel {
            SampleViewModel(
                eventsDispatcher = eventsDispatcherOnMain(),
                permissionsController = permissionsController
            )
        }.also {
            it.eventsDispatcher.bind(this, this)
        }
    }

    fun onRequestButtonClick(view: View?) {
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
        val intent = Intent().apply {
            action = ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
