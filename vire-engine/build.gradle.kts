plugins {
    id("vire.module")
    id("vire.publishing")
}

dependencies {
    implementation(kotlin("stdlib"))

    // Project
    api(projects.vireApi)

    // Libraries
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.kotlinx.coroutines)
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
