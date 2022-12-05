plugins {
    id("dev.icerock.moko.gradle.android.application")
    id("dev.icerock.moko.gradle.detekt")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId = "dev.icerock.moko.samples.permissions"
        minSdk = 21
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompilerVersion.get()
}

dependencies {
    implementation(libs.appCompat)
    implementation(libs.composeActivity)
    implementation(libs.composeMaterial)
    implementation(projects.sample.mppLibrary)
    implementation(projects.permissionsCompose)
}
