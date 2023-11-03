import net.kyori.indra.IndraPlugin
import net.kyori.indra.IndraPublishingPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

plugins {
//    alias(libs.plugins.ktlint)
//    alias(libs.plugins.shadow) apply false
//    alias(libs.plugins.indra)
//    alias(libs.plugins.indra.git)
//    alias(libs.plugins.indra.publishing) apply false
//    alias(libs.plugins.blossom) apply false
    `vire-root`
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

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(layout.buildDirectory.dir("docs"))
}
