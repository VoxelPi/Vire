plugins {
    id("vire.module")
    id("vire.publishing")
}

dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(projects.vireEngine)

    // Annotation processors
    api(libs.gson)

    // Tests
    testImplementation(projects.vireEngine)
    testImplementation(projects.vireStdlib)
}
