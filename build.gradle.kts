plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
}

allprojects {
    group = "net.voxelpi.vire"
    version = "0.3.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        mavenLocal()
    }
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(layout.buildDirectory.dir("docs"))
}
