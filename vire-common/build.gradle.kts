plugins {
    alias(libs.plugins.indra.git)
    alias(libs.plugins.blossom)
}

dependencies {
    implementation(kotlin("stdlib"))

    // Project
    api(project(":vire-api"))

    // Libraries
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.event)

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

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
