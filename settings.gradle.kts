pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "vire"
include("vire-api")
include("vire-engine")
include("vire-stdlib")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
