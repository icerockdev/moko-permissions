import java.net.URI

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()

    jcenter()
    google()

    maven { url = URI("https://icerockdev.bintray.com/moko") }
}

dependencies {
    implementation("dev.icerock:mobile-multiplatform:0.1.0")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
