/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Deps {
    private const val kotlinVersion = "1.4.31"

    private const val androidAppCompatVersion = "1.1.0"
    private const val materialDesignVersion = "1.0.0"
    private const val androidLifecycleVersion = "2.1.0"
    private const val androidCoreTestingVersion = "2.1.0"

    private const val coroutinesVersion = "1.4.2"
    private const val mokoMvvmVersion = "0.9.2"
    const val mokoPermissionsVersion = "0.8.0"

    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 16
    }

    object Plugins {
        val androidApplication = GradlePlugin(id = "com.android.application")
        val androidLibrary = GradlePlugin(id = "com.android.library")
        val kotlinMultiplatform = GradlePlugin(id = "org.jetbrains.kotlin.multiplatform")
        val kotlinKapt = GradlePlugin(id = "kotlin-kapt")
        val kotlinAndroid = GradlePlugin(id = "kotlin-android")
        val mavenPublish = GradlePlugin(id = "org.gradle.maven-publish")
        val signing = GradlePlugin(id = "signing")

        val mobileMultiplatform = GradlePlugin(id = "dev.icerock.mobile.multiplatform")
        val iosFramework = GradlePlugin(id = "dev.icerock.mobile.multiplatform.ios-framework")
    }

    object Libs {
        object Android {
            const val appCompat = "androidx.appcompat:appcompat:$androidAppCompatVersion"
            const val material = "com.google.android.material:material:$materialDesignVersion"
            const val lifecycle = "androidx.lifecycle:lifecycle-extensions:$androidLifecycleVersion"
        }

        object MultiPlatform {
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
            val mokoMvvm = MultiPlatformLibrary(
                common = "dev.icerock.moko:mvvm:$mokoMvvmVersion",
                iosArm64 = "dev.icerock.moko:mvvm-iosarm64:$mokoMvvmVersion",
                iosX64 = "dev.icerock.moko:mvvm-iosx64:$mokoMvvmVersion"
            )
            const val mokoPermissions =
                "dev.icerock.moko:permissions:$mokoPermissionsVersion"
        }

        object Tests {
            const val kotlinTestJUnit =
                "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
            const val androidCoreTesting =
                "androidx.arch.core:core-testing:$androidCoreTestingVersion"
        }
    }
}
