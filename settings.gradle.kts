/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":permissions")
include(":permissions-compose")
include(":permissions-bluetooth")
include(":permissions-camera")
include(":permissions-contacts")
include(":permissions-gallery")
include(":permissions-location")
include(":permissions-avfoundation")
include(":permissions-microphone")
include(":permissions-motion")
include(":permissions-notifications")
include(":permissions-storage")
include(":permissions-test")
include(":sample:android-app")
include(":sample:compose-android-app")
include(":sample:mpp-library")
