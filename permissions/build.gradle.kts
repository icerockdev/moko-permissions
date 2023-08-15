/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
//    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
    id("maven-publish")
}

group = "com.hellomr3"
version = "1.0"

publishing{
    repositories {
        mavenLocal()
    }
}

android {
    namespace = "dev.icerock.moko.permissions"
}

dependencies {
    commonMainImplementation(libs.coroutines)
    androidMainImplementation(libs.appCompat)
    androidMainImplementation(libs.lifecycleRuntime)
}