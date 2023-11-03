plugins {
    id("vire.module")
    id("vire.publishing")
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
