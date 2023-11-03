plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("net.kyori.blossom")
    id("net.kyori.indra")
    id("net.kyori.indra.git")
    `java-library`
}

repositories {
    mavenCentral()
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
