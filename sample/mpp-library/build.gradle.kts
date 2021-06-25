/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
    id("dev.icerock.mobile.multiplatform.ios-framework")
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
    export(project(":permissions"))
    export(libs.mokoMvvmCore)
}
