/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
    id("org.jetbrains.compose")
}

android {
    namespace = "dev.icerock.moko.permissions.compose"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    )

    sourceSets {
        commonMain {
            dependencies {
                api(projects.permissions)
                api(compose.runtime)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.activity)
                implementation(libs.composeUi)
                implementation(libs.lifecycleRuntime)
            }
        }
    }
}
