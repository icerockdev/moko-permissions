/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.android.library")
    id("dev.icerock.moko.gradle.android.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
    id("org.jetbrains.compose")
}

dependencies {
    api(projects.permissions)
    api(compose.runtime)
    api(libs.appCompat)
    api(libs.composeActivity)
}
