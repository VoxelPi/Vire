plugins {
    id("vire.build")
    id("vire.publishing")
}

dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(projects.vireEngine)

    // Tests
    testImplementation(projects.vireEngine)
}
