/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("org.jetbrains.compose")
}

kotlin {
    android()
}

dependencies {
    androidMainApi(projects.permissions)
    androidMainApi(compose.runtime)
    androidMainApi(libs.appCompat)
    androidMainApi(libs.composeActivity)
}

