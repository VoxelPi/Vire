plugins {
    alias(libs.plugins.blossom)
    alias(libs.plugins.indra.git)
}

dependencies {
    implementation(kotlin("stdlib"))

    // Project
    api(project(":vire-api"))

    // Libraries
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.event)
    implementation(libs.kotlin.logging.jvm)

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.platform.launcher)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.log4j.slf4j.impl)
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        blossom {
            kotlinSources {
                property("version", project.version.toString())
                property("git_commit", indraGit.commit()?.name)
            }
        }
    }
}
