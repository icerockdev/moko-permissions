plugins {
    id("dev.icerock.moko.gradle.android.application")
    id("dev.icerock.moko.gradle.detekt")
}

android {
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
}
