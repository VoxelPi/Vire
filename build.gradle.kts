plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
}

allprojects {
    group = "net.voxelpi.vire"
    version = "0.6.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://repo.voxelpi.net/repository/maven-public/") }
        mavenLocal()
    }
}

dokka {
    basePublicationsDirectory.set(layout.buildDirectory.dir("docs"))
}
