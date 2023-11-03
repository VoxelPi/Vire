plugins {
    net.kyori.indra
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
        }

        configurePublications {
            pom {
                developers {
                    developer {
                        id.set("voxelpi")
                        name.set("VoxelPi")
                        url.set("https://voxelpi.dev")
                    }
                }
            }
        }
    }
}
