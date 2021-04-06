/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.iosFramework)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)

    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmCore.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoPermissions)

    androidMainImplementation(Deps.Libs.Android.lifecycle)

    commonTestImplementation(Deps.Libs.MultiPlatform.mokoMvvmTest.common)
    commonTestImplementation(project(":permissions-test"))
}

framework {
    export(project(":permissions"))
    export(Deps.Libs.MultiPlatform.mokoMvvmCore)
}
