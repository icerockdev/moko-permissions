import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS

plugins {
    id("dev.icerock.moko.gradle.android.application")
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.icerockdev"

    defaultConfig {
        applicationId = "dev.icerock.moko.samples.permissions"

        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(libs.androidxCore)
    implementation(libs.material)

    implementation(projects.sample.mppLibrary)
    implementation(projects.permissionsContacts)

    CONFIGURATION_DETEKT_PLUGINS(libs.detekt.cli)
    CONFIGURATION_DETEKT_PLUGINS(libs.detekt.formatting)
}
