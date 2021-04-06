/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mavenPublish)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)

    androidMainImplementation(Deps.Libs.Android.appCompat)

    commonMainApi(project(":permissions"))
}
