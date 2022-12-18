/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(":permissions")
include(":permissions-compose")
include(":permissions-test")
include(":sample:android-app")
include(":sample:compose-android-app")
include(":sample:mpp-library")
