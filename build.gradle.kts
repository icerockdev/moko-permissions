/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

allprojects {
    repositories {
        mavenCentral()
        google()

        jcenter {
            content {
                includeGroup("org.jetbrains.trove4j")
            }
        }
    }

    plugins.withId(Deps.Plugins.androidLibrary.id) {
        configure<com.android.build.gradle.LibraryExtension> {
            compileSdkVersion(Deps.Android.compileSdk)

            defaultConfig {
                minSdkVersion(Deps.Android.minSdk)
                targetSdkVersion(Deps.Android.targetSdk)
            }
        }
    }

    plugins.withId(Deps.Plugins.mavenPublish.id) {
        group = "dev.icerock.moko"
        version = Deps.mokoPermissionsVersion

        val javadocJar by tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
        }

        configure<PublishingExtension> {
            repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "OSSRH"

                credentials {
                    username = System.getenv("OSSRH_USER")
                    password = System.getenv("OSSRH_KEY")
                }
            }

            publications.withType<MavenPublication> {
                // Stub javadoc.jar artifact
                artifact(javadocJar.get())

                // Provide artifacts information requited by Maven Central
                pom {
                    name.set("MOKO permissions")
                    description.set("Runtime permissions controls for mobile (android & ios) Kotlin Multiplatform development")
                    url.set("https://github.com/icerockdev/moko-permissions")
                    licenses {
                        license {
                            url.set("https://github.com/icerockdev/moko-permissions/blob/master/LICENSE.md")
                        }
                    }

                    developers {
                        developer {
                            id.set("Alex009")
                            name.set("Aleksey Mikhailov")
                            email.set("aleksey.mikhailov@icerockdev.com")
                        }
                        developer {
                            id.set("Tetraquark")
                            name.set("Vladislav Areshkin")
                            email.set("vareshkin@icerockdev.com")
                        }
                        developer {
                            id.set("kovalandrew")
                            name.set("Andrew Kovalev")
                            email.set("kovalev@icerockdev.com")
                        }
                        developer {
                            id.set("RezMike")
                            name.set("Mikhail Reznichenko")
                            email.set("mreznichenko@icerockdev.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:ssh://github.com/icerockdev/moko-permissions.git")
                        developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-permissions.git")
                        url.set("https://github.com/icerockdev/moko-permissions")
                    }
                }
            }

            apply(plugin = Deps.Plugins.signing.id)

            configure<SigningExtension> {
                val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
                val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
                val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
                    String(Base64.getDecoder().decode(base64Key))
                }
                if (signingKeyId != null) {
                    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                    sign(publications)
                }
            }
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
