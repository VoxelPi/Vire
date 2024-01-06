plugins {
    id("vire.module")
    id("vire.publishing")
}

dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(projects.vireApi)

    // Tests
    testImplementation(projects.vireEngine)
}
