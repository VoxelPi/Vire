plugins {
    `maven-publish`
}

dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(projects.vireApi)

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.platform.launcher)
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
