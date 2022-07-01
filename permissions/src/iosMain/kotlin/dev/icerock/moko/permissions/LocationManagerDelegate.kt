/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

internal class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    private var callback: ((CLAuthorizationStatus) -> Unit)? = null

    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    fun requestLocationAccess(callback: (CLAuthorizationStatus) -> Unit) {
        this.callback = callback

        locationManager.requestWhenInUseAuthorization()
    }

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        callback?.invoke(didChangeAuthorizationStatus)
        callback = null
    }
}
