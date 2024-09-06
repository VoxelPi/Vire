pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

rootProject.name = "vire"
include("vire-engine")
include("vire-stdlib")
include("vire-serialization")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
