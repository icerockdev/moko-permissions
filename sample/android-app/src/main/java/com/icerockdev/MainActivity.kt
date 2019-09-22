package com.icerockdev

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.icerockdev.library.PermissionsExample
import com.icerockdev.library.mainCoroutineContext
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController

class MainActivity : AppCompatActivity() {

    private lateinit var permissionsExample: PermissionsExample

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prepares the permissions controller and binds it to the activity lifecycle
        val permissionsController = PermissionsController().apply {
            bind(this@MainActivity.lifecycle, supportFragmentManager)
        }

        permissionsExample = PermissionsExample(mainCoroutineContext, permissionsController)
    }

    fun onRequestButtonClick(view: View?) {
        permissionsExample.providePermission(Permission.CAMERA) { exception ->
            if(exception == null) {
                showToast("Permission successfully granted")
            } else {
                exception.printStackTrace()
                showToast("Permission denied")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
