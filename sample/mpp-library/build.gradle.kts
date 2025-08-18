import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform.ios-framework")
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.icerockdev.library"
    compileSdk = 36
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget { publishLibraryVariants("release", "debug") }

    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "MultiPlatformLibrary"
            export(projects.permissions)
            export(libs.mokoMvvmCore)

            linkerOpts.add("-dead_strip")
            linkerOpts.add("-force_load_swift_libs")
            freeCompilerArgs += listOf(
                "-Xbinary=bundleId=dev.icerock.moko.sample.permissions",
            )

            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.permissions)
                api(libs.mokoMvvmCore)
                implementation(libs.coroutines)
                implementation(projects.permissionsContacts)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.lifecycle)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.mokoMvvmTest)
                implementation(projects.permissionsTest)
                implementation(projects.permissionsMicrophone)
            }
        }
    }

    dependencies {
        CONFIGURATION_DETEKT_PLUGINS(libs.detekt.cli)
        CONFIGURATION_DETEKT_PLUGINS(libs.detekt.formatting)
    }
}
