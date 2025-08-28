/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
}

android {
    namespace = "dev.icerock.moko.permissions.test"
}

kotlin {
    jvm("desktop")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting {
            dependsOn(commonMain)
        }
    }
}

dependencies {
    commonMainImplementation(libs.coroutines)

    androidMainImplementation(libs.activity)

    commonMainApi(projects.permissions)
}
