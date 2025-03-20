plugins {
    id("vire.build")
    id("vire.publish")
}

dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(projects.vireEngine)

    // Tests
    testImplementation(projects.vireEngine)
}
