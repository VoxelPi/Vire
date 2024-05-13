plugins {
    id("vire.module")
    id("vire.publishing")
    alias(libs.plugins.ksp)
}

dependencies {
    compileOnly(kotlin("stdlib"))

    // Project
    compileOnly(projects.vireEngine)
    implementation(libs.bundles.moshi)

    // Annotation processors
    ksp(libs.moshi.codegen)

    // Tests
    testImplementation(projects.vireEngine)
    testImplementation(projects.vireStdlib)
}

ktlint {
}
