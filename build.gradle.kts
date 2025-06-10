/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.androidGradlePlugin)
        classpath(libs.mokoGradlePlugin)
        classpath(libs.mobileMultiplatformGradlePlugin)
        classpath(libs.kotlinSerializationGradlePlugin)
        classpath(libs.composeJetBrainsGradlePlugin)
        classpath(libs.detektGradlePlugin)
    }
}

apply(plugin = "dev.icerock.moko.gradle.publication.nexus")
val mokoVersion = libs.versions.mokoPermissionsVersion.get()
allprojects {
    group = "dev.icerock.moko"
    version = mokoVersion
    
    // Configure Java toolchain to use Java 17
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
            jvmToolchain(17)
        }
    }
    
    // For Android projects
    plugins.withId("com.android.application") {
        extensions.configure<com.android.build.gradle.BaseExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
    
    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.gradle.LibraryExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
