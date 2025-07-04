/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.gradle.utils.connectTargetsToSourceSet
import dev.icerock.moko.gradle.utils.createMainTest
import dev.icerock.moko.gradle.utils.setupDependency
import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.moko.gradle.publication")
    alias(libs.plugins.detekt)
}

kotlin {
    applyDefaultHierarchyTemplate()

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
