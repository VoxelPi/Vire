plugins {
    org.jetbrains.kotlin.jvm
    org.jetbrains.dokka
    org.jlleitschuh.gradle.ktlint
    net.kyori.blossom
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

//    ktlint {
//        version.set("1.0.0")
//        verbose.set(true)
//        outputToConsole.set(true)
//        coloredOutput.set(true)
//        reporters {
//            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
//        }
//    }
}
