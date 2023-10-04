plugins {
    `maven-publish`
}

dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(project(":vire-api"))

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}
