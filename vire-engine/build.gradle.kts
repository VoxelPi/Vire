plugins {
    id("vire.module")
    id("vire.publishing")
}

dependencies {
    implementation(kotlin("stdlib"))

    // Libraries
    api(libs.event)
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging.jvm)
}

sourceSets {
    main {
        blossom {
            kotlinSources {
                property("version", project.version.toString())
                property("git_commit", indraGit.commit()?.name)
            }
        }
    }
}
