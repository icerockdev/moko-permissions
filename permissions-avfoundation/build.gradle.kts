/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.gradle.utils.connectTargetsToSourceSet
import dev.icerock.moko.gradle.utils.createMainTest
import dev.icerock.moko.gradle.utils.setupDependency

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.detekt")
}

kotlin {
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    with(this.sourceSets) {
        // creation
        createMainTest("ios")

        // ios dependencies
        setupDependency("ios", "common")
        connectTargetsToSourceSet(
            targetNames = listOf("iosX64", "iosArm64", "iosSimulatorArm64"),
            sourceSetPrefix = "ios"
        )
    }
}

dependencies {
    commonMainApi(projects.permissions)
    commonMainImplementation(libs.coroutines)
}
