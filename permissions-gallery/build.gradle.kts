import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS

/*
 * Copyright 2024 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    alias(libs.plugins.detekt)
}

android {
    namespace = "dev.icerock.moko.permissions.gallery"
    compileSdk = 36
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget { publishLibraryVariants("release", "debug") }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    )

    sourceSets {
        commonMain {
            dependencies {
                api(projects.permissions)
                implementation(libs.coroutines)
            }
        }
    }

    dependencies {
        CONFIGURATION_DETEKT_PLUGINS(libs.detekt.cli)
        CONFIGURATION_DETEKT_PLUGINS(libs.detekt.formatting)
    }
}
