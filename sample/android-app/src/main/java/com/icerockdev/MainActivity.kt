package com.icerockdev

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.icerockdev.library.PermissionsHandler
import com.icerockdev.library.mainCoroutineContext
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.content.Intent
import android.net.Uri

class MainActivity : AppCompatActivity() {

    private lateinit var permissionsHandler: PermissionsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prepares the permissions controller and binds it to the activity lifecycle.
        val permissionsController = PermissionsController().apply {
            bind(this@MainActivity.lifecycle, supportFragmentManager)
        }

        permissionsHandler = PermissionsHandler(mainCoroutineContext, permissionsController)
    }

    fun onRequestButtonClick(view: View?) {
        // Starts permission providing process.
        permissionsHandler.providePermission(Permission.CAMERA, object :
            PermissionsHandler.PermissionsProviderListener {

            override fun onSuccess() {
                onPermissionGranted()
            }

            override fun onDenied(exception: DeniedException) {
                onPermissionDenied(exception)
            }

            override fun onDeniedAlways(exception: DeniedAlwaysException) {
                onPermissionDeniedAlways(exception)
            }
        })
    }

    private fun onPermissionGranted() {
        showToast("Permission successfully granted!")
    }

    private fun onPermissionDenied(deniedException: DeniedException) {
        showToast("Permission denied!")
    }

    private fun onPermissionDeniedAlways(deniedAlwaysException: DeniedAlwaysException) {
        Snackbar
            .make(findViewById<LinearLayout>(R.id.root_view), "Permission is always denied", Snackbar.LENGTH_LONG)
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
