plugins {
    id("dev.icerock.moko.gradle.android.application")
    id("dev.icerock.moko.gradle.detekt")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.icerockdev"

    defaultConfig {
        applicationId = "dev.icerock.moko.samples.permissions"
        minSdk = 21
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(libs.androidxCore)
    implementation(libs.composeActivity)
    implementation(libs.composeMaterial)
    implementation(projects.sample.mppLibrary)
    implementation(projects.permissionsCompose)
    implementation(projects.permissionsCamera)
}
