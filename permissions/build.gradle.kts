/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
}

android {
    namespace = "dev.icerock.moko.permissions"
    compileSdk = 36
}

kotlin {
    applyDefaultHierarchyTemplate()
    
    androidTarget ()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    )

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coroutines)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.activity)
                implementation(libs.lifecycleRuntime)
            }
        }
    }
}
