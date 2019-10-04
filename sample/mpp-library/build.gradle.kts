plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
}

setupFramework(
    exports = listOf(
        Deps.Libs.MultiPlatform.mokoPermissions
    )
)

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
    mppLibrary(Deps.Libs.MultiPlatform.coroutines)

    // Adds mokoPermissions from Bintray:
    // mppLibrary(Deps.Libs.MultiPlatform.mokoPermissions)

    // Adds mokoPermissions locally as module (for the sample purposes):
    mppModule(MultiPlatformModule(":permissions"))
}
