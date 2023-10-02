dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(project(":vire-api"))

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}
