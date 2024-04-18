/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
    id("org.jetbrains.compose")
}

android {
    namespace = "dev.icerock.moko.permissions.compose"

    defaultConfig {
        minSdk = 21
    }
}

dependencies {
    commonMainApi(projects.permissions)
    commonMainApi(compose.runtime)
    androidMainImplementation(libs.activity)
    androidMainImplementation(libs.composeUi)
    androidMainImplementation(libs.lifecycleRuntime)
}
