/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.mobile.multiplatform.ios-framework")
    id("dev.icerock.moko.gradle.detekt")
}

dependencies {
    commonMainImplementation(libs.coroutines)

    commonMainApi(libs.mokoMvvmCore)
    commonMainApi(projects.permissions)

    androidMainImplementation(libs.lifecycle)

    commonTestImplementation(libs.mokoMvvmTest)
    commonTestImplementation(projects.permissionsTest)
}

framework {
    export(projects.permissions)
    export(libs.mokoMvvmCore)
}
