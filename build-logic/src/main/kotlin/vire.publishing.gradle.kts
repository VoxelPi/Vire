plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

val javadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

 val sourcesJar by tasks.creating(Jar::class) {
     archiveClassifier.set("sources")
     from(sourceSets.getByName("main").allSource)
 }

 publishing {
     repositories {
         maven {
             name = "GitHubPackages"
             url = uri("https://maven.pkg.github.com/voxelpi/vire")
             credentials {
                 username = System.getenv("GITHUB_ACTOR")
                 password = System.getenv("GITHUB_TOKEN")
             }
         }
     }

     publications {
         create<MavenPublication>("maven") {
             groupId = project.group.toString()
             artifactId = project.name
             version = project.version.toString()

             from(components["kotlin"])
             artifact(sourcesJar)
             artifact(javadocJar)

             pom {
                 name = project.name
                 description = project.description
                 url = "https://github.com/voxelpi/vire"

                 licenses {
                     license {
                         name = "The MIT License"
                         url = "https://opensource.org/licenses/MIT"
                     }
                 }

                 developers {
                     developer {
                         id = "voxelpi"
                         name = "Peter Smek"
                         url = "https://voxelpi.net"
                     }
                 }

                 scm {
                     connection = "scm:git:https://github.com/voxelpi/vire.git"
                     developerConnection = "scm:git:ssh://git@github.com/voxelpi/vire.git"
                     url = "https://github.com/voxelpi/vire"
                 }

                 issueManagement {
                     system = "GitHub"
                     url = "https://github.com/voxelpi/vire/issues"
                 }

                 ciManagement {
                     system = "GitHub Actions"
                     url = "https://github.com/voxelpi/vire/actions"
                 }
             }
         }
     }
 }

signing {
    val signingSecretKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingSecretKey, signingPassword)
    sign(publishing.publications)
}
