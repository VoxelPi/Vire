import net.kyori.indra.IndraPlugin
import net.kyori.indra.IndraPublishingPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
//    alias(libs.plugins.ktlint) // TODO: Disable until https://github.com/JLLeitschuh/ktlint-gradle/issues/692 is fixed.
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.indra)
    alias(libs.plugins.indra.git)
    alias(libs.plugins.indra.publishing) apply false
    alias(libs.plugins.blossom) apply false
}

allprojects {
    group = "net.voxelpi.vire"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        mavenLocal()
    }
}

subprojects {
    apply<KotlinPluginWrapper>()
    apply<DokkaPlugin>()
    apply<IndraPlugin>()
    apply<IndraPublishingPlugin>()
//    apply(plugin = "org.jlleitschuh.gradle.ktlint") // TODO: Disable until https://github.com/JLLeitschuh/ktlint-gradle/issues/692 is fixed.

    kotlin {
        jvmToolchain(17)
    }

    tasks {
        val javaVersion = JavaVersion.VERSION_17

        compileKotlin {
            kotlinOptions {
                jvmTarget = javaVersion.toString()
                compilerOptions {}
            }
        }

        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
            }
        }

        kotlin {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
            }
        }

        dokkaHtml.configure {
            outputDirectory.set(layout.buildDirectory.dir("docs"))
        }

        indra {
            mitLicense()

            javaVersions {
                target(javaVersion.toString().toInt())
            }

            github("VoxelPi", "Vire") {
                ci(true)
                issues(true)
            }

            configurePublications {
                pom {
                    developers {
                        developer {
                            id.set("voxelpi")
                            name.set("VoxelPi")
                            url.set("https://voxelpi.dev")
                        }
                    }
                }
            }
        }
    }

// TODO: Disable until https://github.com/JLLeitschuh/ktlint-gradle/issues/692 is fixed.
//    ktlint {
//        verbose.set(true)
//        outputToConsole.set(true)
//        coloredOutput.set(true)
//        disabledRules.set(setOf("trailing-comma-on-declaration-site", "trailing-comma-on-call-site", "spacing-between-declarations-with-comments"))
//        reporters {
//            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
//        }
//    }
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(layout.buildDirectory.dir("docs"))
}
