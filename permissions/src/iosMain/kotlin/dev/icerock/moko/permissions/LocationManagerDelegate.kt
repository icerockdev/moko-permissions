/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.permissions

import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

internal expect class LocationManagerDelegate() : NSObject, CLLocationManagerDelegateProtocol {
    fun requestLocationAccess(callback: (CLAuthorizationStatus) -> Unit)
}
