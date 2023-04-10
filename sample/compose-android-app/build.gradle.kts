plugins {
    id("dev.icerock.moko.gradle.android.application")
    id("dev.icerock.moko.gradle.detekt")
    id("org.jetbrains.compose")
}

android {
    defaultConfig {
        applicationId = "dev.icerock.moko.samples.permissions"
        minSdk = 21
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(libs.appCompat)
    implementation(libs.composeActivity)
    implementation(libs.composeMaterial)
    implementation(projects.sample.mppLibrary)
    implementation(projects.permissionsCompose)
}
