import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS

/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    alias(libs.plugins.detekt)
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

    dependencies {
        CONFIGURATION_DETEKT_PLUGINS(libs.detekt.cli)
        CONFIGURATION_DETEKT_PLUGINS(libs.detekt.formatting)
    }
}
