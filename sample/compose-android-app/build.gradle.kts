import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS

plugins {
    id("dev.icerock.moko.gradle.android.application")
    alias(libs.plugins.detekt)
    id("org.jetbrains.kotlin.plugin.compose")
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

    CONFIGURATION_DETEKT_PLUGINS(libs.detekt.cli)
    CONFIGURATION_DETEKT_PLUGINS(libs.detekt.formatting)
}
