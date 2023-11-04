plugins {
    id("vire.module")
    id("vire.publishing")
}

dependencies {
    compileOnlyApi(kotlin("stdlib"))
    compileOnlyApi(libs.kotlin.reflect)
    compileOnlyApi(libs.bundles.kotlinx.coroutines)

    // Libraries
    compileOnlyApi(libs.slf4j.api)
    compileOnlyApi(libs.kotlin.logging.jvm)
}
