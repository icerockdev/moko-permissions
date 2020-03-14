plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()

    jcenter()
    google()

    maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
}

dependencies {
    implementation("dev.icerock:mobile-multiplatform:0.6.0")
    implementation("com.android.tools.build:gradle:3.6.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.70")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
