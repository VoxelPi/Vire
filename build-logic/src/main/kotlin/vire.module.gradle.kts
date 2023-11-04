import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("net.kyori.blossom")
    id("net.kyori.indra.git")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.platform.launcher)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.log4j.slf4j.impl)
}

kotlin {
    jvmToolchain(17)
}

tasks {
    dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("docs"))
    }

    test {
        useJUnitPlatform()
    }

    ktlint {
        version.set("1.0.0")
        verbose.set(true)
        outputToConsole.set(true)
        coloredOutput.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
    }
}
