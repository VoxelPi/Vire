plugins {
    id("net.kyori.indra.publishing")
}

tasks {
    indra {
        mitLicense()

        javaVersions {
            target(17)
        }

        github("VoxelPi", "Vire") {
            ci(true)
            issues(true)
            publishing(true)
        }

        configurePublications {
            pom {
                developers {
                    developer {
                        id.set("voxelpi")
                        name.set("Peter Smek")
                        url.set("https://voxelpi.net")
                    }
                }
            }
        }
    }
}
